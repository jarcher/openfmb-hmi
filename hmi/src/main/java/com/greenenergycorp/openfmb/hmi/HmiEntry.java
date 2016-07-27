/**
 * Copyright 2016 Green Energy Corp.
 *
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Green Energy
 * Corp licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.greenenergycorp.openfmb.hmi;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.greenenergycorp.openfmb.mapping.adapter.PayloadObserver;
import com.greenenergycorp.openfmb.mapping.data.xml.OpenFmbXmlMarshaller;
import com.greenenergycorp.openfmb.mapping.mqtt.MqttAdapterManager;
import com.greenenergycorp.openfmb.mapping.mqtt.MqttConfiguration;
import com.greenenergycorp.openfmb.mapping.mqtt.MqttObserver;
import com.greenenergycorp.openfmb.simulator.DeviceId;
import com.greenenergycorp.openfmb.simulator.PropertyUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionStage;


public class HmiEntry {

    public static void main(String[] args) throws Exception {

        final String openfmbConfigPath = System.getProperty("config.hmi.path", "hmi.properties");
        final Properties openfmbProps = PropertyUtil.optionallyLoad(openfmbConfigPath, System.getProperties());
        
        final String recloserLogicalDeviceId = PropertyUtil.propOrThrow(openfmbProps, "device.recloser.logicalDeviceID");
        final String reclosermRid = PropertyUtil.propOrThrow(openfmbProps, "device.recloser.mRID");
        final String recloserName = PropertyUtil.propOrThrow(openfmbProps, "device.recloser.name");
        final String recloserDescription = PropertyUtil.propOrThrow(openfmbProps, "device.recloser.description");
        final DeviceId recloserDeviceId = new DeviceId(recloserLogicalDeviceId, reclosermRid, recloserName, recloserDescription);

        final String batteryLogicalDeviceId = PropertyUtil.propOrThrow(openfmbProps, "device.battery.logicalDeviceID");
        final String batterymRid = PropertyUtil.propOrThrow(openfmbProps, "device.battery.mRID");
        final String batteryName = PropertyUtil.propOrThrow(openfmbProps, "device.battery.name");
        final String batteryDescription = PropertyUtil.propOrThrow(openfmbProps, "device.battery.description");
        final DeviceId batteryDeviceId = new DeviceId(batteryLogicalDeviceId, batterymRid, batteryName, batteryDescription);

        final String recloserEventTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.RecloserEventProfile");
        final String recloserReadTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.RecloserReadingProfile");
        final String recloserControlTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.RecloserControlProfile");

        final String batteryReadTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.BatteryReadingProfile");
        final String batteryEventTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.BatteryEventProfile");
        final String batteryControlTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.BatteryControlProfile");
        final String resourceReadTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.ResourceReadingProfile");
        final String solarReadTopic = PropertyUtil.propOrThrow(openfmbProps, "topic.SolarReadingProfile");

        final long timeoutMs = PropertyUtil.propLongOrThrow(openfmbProps, "config.timeoutMs");

        final OpenFmbXmlMarshaller openFmbXmlMarshaller = new OpenFmbXmlMarshaller();

        final ActorSystem system = ActorSystem.create();

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final StateManager stateManager = new StateManager(timeoutMs);

        final String mqttConfigPath = System.getProperty("config.mqtt.path", "mqtt.properties");

        final MqttConfiguration mqttConfiguration = MqttConfiguration.fromFile(mqttConfigPath);

        final MqttAdapterManager mqttAdapterManager = new MqttAdapterManager(mqttConfiguration, 0);

        final MqttObserver mqttObserver = mqttAdapterManager.getMessageObserver();

        final ControlIssuer controlIssuer = new ControlIssuer(recloserDeviceId, recloserControlTopic, batteryDeviceId, batteryControlTopic, mqttObserver, openFmbXmlMarshaller);

        final HmiServer app = new HmiServer(controlIssuer, stateManager);

        final Thread mqttThread = new Thread(new Runnable() {
            public void run() {
                mqttAdapterManager.run();
            }
        }, "mqtt publisher");

        final Map<String, PayloadObserver> controlHandlerMap = new HashMap<String, PayloadObserver>();
        controlHandlerMap.put(recloserReadTopic + "/#", new MqttSubscribers.RecloserReadSubscriber(stateManager, openFmbXmlMarshaller));
        controlHandlerMap.put(recloserEventTopic + "/#", new MqttSubscribers.RecloserEventSubscriber(stateManager, openFmbXmlMarshaller));
        controlHandlerMap.put(batteryReadTopic + "/#", new MqttSubscribers.BatteryReadSubscriber(stateManager, openFmbXmlMarshaller));
        controlHandlerMap.put(batteryEventTopic + "/#", new MqttSubscribers.BatteryEventSubscriber(stateManager, openFmbXmlMarshaller));
        controlHandlerMap.put(solarReadTopic + "/#", new MqttSubscribers.SolarReadSubscriber(stateManager, openFmbXmlMarshaller));
        controlHandlerMap.put(resourceReadTopic + "/#", new MqttSubscribers.ResourceReadSubscriber(stateManager, openFmbXmlMarshaller));

        mqttAdapterManager.subscribe(controlHandlerMap);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8080), materializer);

        mqttThread.start();

        System.out.println("ctrl-c to exit");
        System.in.read();

        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());

    }
}
