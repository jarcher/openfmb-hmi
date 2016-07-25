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

import com.greenenergycorp.openfmb.mapping.data.xml.OpenFmbXmlMarshaller;
import com.greenenergycorp.openfmb.mapping.mqtt.MqttObserver;
import com.greenenergycorp.openfmb.simulator.DeviceId;
import com.greenenergycorp.openfmb.xml.BatteryControlProfile;
import com.greenenergycorp.openfmb.xml.RecloserControlProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlIssuer {

    private final static Logger logger = LoggerFactory.getLogger(ControlIssuer.class);

    private final DeviceId recloserId;
    private final String baseRecloserTopic;
    private final DeviceId batteryId;
    private final String baseBatteryTopic;
    private final MqttObserver observer;
    private final OpenFmbXmlMarshaller marshaller;

    public ControlIssuer(DeviceId recloserId, String baseRecloserTopic, DeviceId batteryId, String baseBatteryTopic, MqttObserver observer, OpenFmbXmlMarshaller marshaller) {
        this.recloserId = recloserId;
        this.baseRecloserTopic = baseRecloserTopic;
        this.batteryId = batteryId;
        this.baseBatteryTopic = baseBatteryTopic;
        this.observer = observer;
        this.marshaller = marshaller;
    }

    public void tripRecloser() {
        issueRecloser("trip");
    }

    public void closeRecloser() {
        issueRecloser("close");
    }

    private void issueRecloser(final String action) {
        try {
            final RecloserControlProfile profile = XmlModel.buildRecloserControlAction(recloserId, action);
            final byte[] bytes = marshaller.marshal(profile);
            if (bytes != null) {
                observer.publish(bytes, baseRecloserTopic + "/" + recloserId.getLogicalDeviceId());
            } else {
                logger.warn("Null object being published");
            }

        } catch (Throwable ex) {
            logger.warn("Error issuing control: " + ex);
        }
    }

    public void setBatteryPowerSetpoint(final double value) {
        try {
            final BatteryControlProfile profile = XmlModel.buildBatteryControlPowerSetpoint(batteryId, value);
            final byte[] bytes = marshaller.marshal(profile);
            if (bytes != null) {
                observer.publish(bytes, baseBatteryTopic + "/" + batteryId.getLogicalDeviceId());
            } else {
                logger.warn("Null object being published");
            }

        } catch (Throwable ex) {
            logger.warn("Error issuing control: " + ex);
        }
    }


    public void setBatteryMode(final int mode) {
        try {
            final BatteryControlProfile profile = XmlModel.buildBatteryControlModeSetpoint(batteryId, mode);
            final byte[] bytes = marshaller.marshal(profile);
            if (bytes != null) {
                observer.publish(bytes, baseBatteryTopic + "/" + batteryId.getLogicalDeviceId());
            } else {
                logger.warn("Null object being published");
            }
        } catch (Throwable ex) {
            logger.warn("Error issuing control: " + ex);
        }
    }


}
