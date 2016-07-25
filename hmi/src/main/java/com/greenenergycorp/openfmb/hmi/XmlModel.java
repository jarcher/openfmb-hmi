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

import com.greenenergycorp.openfmb.mapping.data.xml.CommonMapping;
import com.greenenergycorp.openfmb.simulator.DeviceId;
import com.greenenergycorp.openfmb.xml.*;

import javax.xml.datatype.XMLGregorianCalendar;

public class XmlModel {

    public static Recloser buildRecloserDescription(final DeviceId id) {
        final Recloser description = new Recloser();
        description.setMRID(id.getmRid());
        description.setName(id.getName());
        description.setDescription(id.getDescription());
        description.setNormalOpen(false);
        return description;
    }

    public static RecloserControlProfile buildRecloserControlAction(final DeviceId id, final String action) throws Exception {

        final long now = System.currentTimeMillis();
        final XMLGregorianCalendar calendarNow = CommonMapping.xmlTimeFor(now);

        final RecloserControlProfile profile = new RecloserControlProfile();
        profile.setLogicalDeviceID(id.getLogicalDeviceId());
        profile.setTimestamp(calendarNow);

        final Recloser description = buildRecloserDescription(id);
        profile.setRecloser(description);

        final EndDeviceControlType controlType = new EndDeviceControlType();
        controlType.setAction(action);
        controlType.setType("");

        final RecloserControl control = new RecloserControl();
        control.setEndDeviceControlType(controlType);

        profile.setRecloserControl(control);

        return profile;
    }


    public static BatterySystem buildBatteryDescription(final DeviceId id) {
        final BatterySystem description = new BatterySystem();
        description.setMRID(id.getmRid());
        description.setName(id.getName());
        description.setDescription(id.getDescription());
        return description;
    }

    public static BatteryControlProfile buildBatteryControlIsIslanded(final DeviceId id) throws Exception {

        final long now = System.currentTimeMillis();
        final XMLGregorianCalendar calendarNow = CommonMapping.xmlTimeFor(now);

        final BatteryControlProfile profile = new BatteryControlProfile();
        profile.setLogicalDeviceID(id.getLogicalDeviceId());
        profile.setTimestamp(calendarNow);

        profile.setBatterySystem(buildBatteryDescription(id));

        final BatterySystemControl batteryControl = new BatterySystemControl();
        batteryControl.setIsIslanded(true);

        profile.setBatterySystemControl(batteryControl);

        return profile;
    }

    public static BatteryControlProfile buildBatteryControlPowerSetpoint(final DeviceId id, final double power) throws Exception {

        final long now = System.currentTimeMillis();
        final XMLGregorianCalendar calendarNow = CommonMapping.xmlTimeFor(now);

        final BatteryControlProfile profile = new BatteryControlProfile();
        profile.setLogicalDeviceID(id.getLogicalDeviceId());
        profile.setTimestamp(calendarNow);

        profile.setBatterySystem(buildBatteryDescription(id));

        final BatterySystemControl batteryControl = new BatterySystemControl();
        batteryControl.setIsIslanded(false);

        final SetPoint setPoint = new SetPoint();
        setPoint.setUnit(UnitSymbolKind.W);
        setPoint.setMultiplier(UnitMultiplierKind.KILO);
        setPoint.setControlType("SetRealPower");
        setPoint.setValue((float)power);

        batteryControl.getSetPoints().add(setPoint);

        profile.setBatterySystemControl(batteryControl);

        return profile;
    }

    public static BatteryControlProfile buildBatteryControlModeSetpoint(final DeviceId id, final int mode) throws Exception {

        final long now = System.currentTimeMillis();
        final XMLGregorianCalendar calendarNow = CommonMapping.xmlTimeFor(now);

        final BatteryControlProfile profile = new BatteryControlProfile();
        profile.setLogicalDeviceID(id.getLogicalDeviceId());
        profile.setTimestamp(calendarNow);

        profile.setBatterySystem(buildBatteryDescription(id));

        final BatterySystemControl batteryControl = new BatterySystemControl();
        batteryControl.setIsIslanded(false);

        final SetPoint setPoint = new SetPoint();
        setPoint.setUnit(UnitSymbolKind.NO_UNIT);
        setPoint.setMultiplier(UnitMultiplierKind.NO_MULTIPLIER);
        setPoint.setControlType("SetMode");
        setPoint.setValue((float)mode);

        batteryControl.getSetPoints().add(setPoint);

        profile.setBatterySystemControl(batteryControl);

        return profile;
    }
}
