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

import com.greenenergycorp.openfmb.mapping.adapter.PayloadObserver;
import com.greenenergycorp.openfmb.mapping.data.xml.OpenFmbXmlMarshaller;
import com.greenenergycorp.openfmb.xml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttSubscribers {


    public static class RecloserReadSubscriber implements PayloadObserver {
        private final static Logger logger = LoggerFactory.getLogger(RecloserReadSubscriber.class);

        private final CircuitStateObserver observer;
        private final OpenFmbXmlMarshaller marshaller;

        public RecloserReadSubscriber(CircuitStateObserver observer, OpenFmbXmlMarshaller marshaller) {
            this.observer = observer;
            this.marshaller = marshaller;
        }

        public void handle(byte[] bytes) {
            try {
                final Object object = marshaller.unmarshal(bytes);
                if (object instanceof RecloserReadingProfile) {
                    final RecloserReadingProfile profile = (RecloserReadingProfile) object;

                    final String logicalDeviceId = profile.getLogicalDeviceID();

                    Double power = null;
                    double voltage = 0.0;
                    double frequency = 0.0;
                    for (final Reading r: profile.getReadings()) {
                        if (r.getReadingType().getUnit() == UnitSymbolKind.W) {
                            power = (double)r.getValue();
                        }
                        if (r.getReadingType().getUnit() == UnitSymbolKind.V) {
                            voltage = (double)r.getValue();
                        }
                        if (r.getReadingType().getUnit() == UnitSymbolKind.HZ) {
                            frequency = (double)r.getValue();
                        }

                        if (power != null){
                            observer.updateRecloserReadings(logicalDeviceId, power, voltage, frequency);
                        }
                    }
                }
            } catch (Throwable ex) {
                logger.warn("Error handling reading: " + ex);
            }
        }
    }

    public static class RecloserEventSubscriber implements PayloadObserver {
        private final static Logger logger = LoggerFactory.getLogger(RecloserEventSubscriber.class);

        private final CircuitStateObserver observer;
        private final OpenFmbXmlMarshaller marshaller;

        public RecloserEventSubscriber(CircuitStateObserver observer, OpenFmbXmlMarshaller marshaller) {
            this.observer = observer;
            this.marshaller = marshaller;
        }

        public void handle(byte[] bytes) {
            try {

                final Object object = marshaller.unmarshal(bytes);
                if (object instanceof RecloserEventProfile) {
                    final RecloserEventProfile profile = (RecloserEventProfile) object;

                    final String logicalDeviceId = profile.getLogicalDeviceID();

                    if (profile.getRecloserStatus() != null) {
                        observer.updateRecloserStatus(logicalDeviceId, profile.getRecloserStatus().getSwitchStatus() == SwitchStatusKind.CLOSED);
                    }
                }
            } catch (Throwable ex) {
                logger.warn("Error handling reading: " + ex);
            }
        }
    }

    public static class BatteryReadSubscriber implements PayloadObserver {
        private final static Logger logger = LoggerFactory.getLogger(BatteryReadSubscriber.class);

        private final CircuitStateObserver observer;
        private final OpenFmbXmlMarshaller marshaller;

        public BatteryReadSubscriber(CircuitStateObserver observer, OpenFmbXmlMarshaller marshaller) {
            this.observer = observer;
            this.marshaller = marshaller;
        }


        public void handle(byte[] bytes) {
            try {

                final Object object = marshaller.unmarshal(bytes);
                if (object instanceof BatteryReadingProfile) {
                    final BatteryReadingProfile profile = (BatteryReadingProfile) object;

                    final String logicalDeviceId = profile.getLogicalDeviceID();

                    Double power = null;
                    double voltage = 0.0;
                    double frequency = 0.0;
                    for (final Reading r: profile.getReadings()) {
                        if (r.getReadingType().getUnit() == UnitSymbolKind.W) {
                            power = (double)r.getValue();
                        }
                        if (r.getReadingType().getUnit() == UnitSymbolKind.V) {
                            voltage = (double)r.getValue();
                        }
                        if (r.getReadingType().getUnit() == UnitSymbolKind.HZ) {
                            frequency = (double)r.getValue();
                        }

                        if (power != null){
                            observer.updateBatteryReadings(logicalDeviceId, power, voltage, frequency);
                        }
                    }
                }
            } catch (Throwable ex) {
                logger.warn("Error handling reading: " + ex);
            }
        }
    }

    public static class BatteryEventSubscriber implements PayloadObserver {
        private final static Logger logger = LoggerFactory.getLogger(BatteryEventSubscriber.class);

        private final CircuitStateObserver observer;
        private final OpenFmbXmlMarshaller marshaller;

        public BatteryEventSubscriber(CircuitStateObserver observer, OpenFmbXmlMarshaller marshaller) {
            this.observer = observer;
            this.marshaller = marshaller;
        }


        public void handle(byte[] bytes) {
            try {

                final Object object = marshaller.unmarshal(bytes);
                if (object instanceof BatteryEventProfile) {
                    final BatteryEventProfile profile = (BatteryEventProfile) object;

                    final String logicalDeviceId = profile.getLogicalDeviceID();

                    if (profile.getBatteryStatus() != null) {
                        final Boolean isCharging = profile.getBatteryStatus().isIsCharging();
                        final Float stateOfCharge = profile.getBatteryStatus().getStateOfCharge();
                        final String mode = profile.getBatteryStatus().getMode();
                        observer.updateBatteryStatus(logicalDeviceId, isCharging, stateOfCharge, mode);
                    }
                }
            } catch (Throwable ex) {
                logger.warn("Error handling reading: " + ex);
            }
        }
    }

    public static class SolarReadSubscriber implements PayloadObserver {
        private final static Logger logger = LoggerFactory.getLogger(SolarReadSubscriber.class);

        private final CircuitStateObserver observer;
        private final OpenFmbXmlMarshaller marshaller;

        public SolarReadSubscriber(CircuitStateObserver observer, OpenFmbXmlMarshaller marshaller) {
            this.observer = observer;
            this.marshaller = marshaller;
        }


        public void handle(byte[] bytes) {
            try {

                final Object object = marshaller.unmarshal(bytes);
                if (object instanceof SolarReadingProfile) {
                    final SolarReadingProfile profile = (SolarReadingProfile) object;

                    final String logicalDeviceId = profile.getLogicalDeviceID();

                    for (final Reading r: profile.getReadings()) {
                        if (r.getReadingType().getUnit() == UnitSymbolKind.W) {
                            final double value = (double) r.getValue();
                            observer.updateSolarPower(logicalDeviceId, value);
                        }
                    }
                }
            } catch (Throwable ex) {
                logger.warn("Error handling reading: " + ex);
            }
        }
    }

    public static class ResourceReadSubscriber implements PayloadObserver {
        private final static Logger logger = LoggerFactory.getLogger(ResourceReadSubscriber.class);

        private final CircuitStateObserver observer;
        private final OpenFmbXmlMarshaller marshaller;

        public ResourceReadSubscriber(CircuitStateObserver observer, OpenFmbXmlMarshaller marshaller) {
            this.observer = observer;
            this.marshaller = marshaller;
        }


        public void handle(byte[] bytes) {
            try {

                final Object object = marshaller.unmarshal(bytes);
                if (object instanceof ResourceReadingProfile) {
                    final ResourceReadingProfile profile = (ResourceReadingProfile) object;

                    final String logicalDeviceId = profile.getLogicalDeviceID();

                    for (final Reading r: profile.getReadings()) {
                        if (r.getReadingType().getUnit() == UnitSymbolKind.W) {
                            final double value = (double) r.getValue();
                            observer.updateLoadPower(logicalDeviceId, value);
                        }
                    }
                }
            } catch (Throwable ex) {
                logger.warn("Error handling reading: " + ex);
            }
        }
    }
}
