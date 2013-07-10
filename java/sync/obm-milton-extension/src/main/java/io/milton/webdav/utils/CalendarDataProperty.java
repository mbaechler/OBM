/*
 * Copyright 2013 McEvoy Software Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.milton.webdav.utils;

import io.milton.http.Response;
import io.milton.http.values.CData;
import io.milton.http.webdav.PropertyMap.StandardProperty;
import io.milton.resource.ICalResource;
import io.milton.resource.PropFindableResource;

/**
 *
 * @author brad
 */
public class CalendarDataProperty implements StandardProperty<CData> {

    @Override
    public String fieldName() {
        return "calendar-data";
    }

    @Override
    public CData getValue(PropFindableResource res) {
        return LockUtils.getCalendarValue(res);
    }

    @Override
    public Class<CData> getValueClass() {
        return CData.class;
    }
}
