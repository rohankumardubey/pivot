/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.json.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pivot.beans.BeanAdapter;
import org.apache.pivot.io.SerializationException;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.json.JSONSerializerListener;
import org.junit.Test;

public class JSONSerializerTest {
    @Test
    public void testCarriageReturns() {
        List<?> emptyList;
        try {
            emptyList = JSONSerializer.parseList("[\n]");
        } catch(SerializationException exception) {
            throw new RuntimeException(exception);
        }

        assertEquals(0, emptyList.size());
    }

    @Test
    public void testE() throws SerializationException {
        assertEquals(5000000, JSONSerializer.parseDouble("5.0E6"), 0);
        assertEquals(0.000005, JSONSerializer.parseDouble("5.0E-6"), 0);
    }

    @Test(expected=SerializationException.class)
    public void testFloatNaN() throws SerializationException {
        JSONSerializer.toString(Float.NaN);
    }

    @Test(expected=SerializationException.class)
    public void testFloatNegativeInfinity() throws SerializationException {
        JSONSerializer.toString(Float.NEGATIVE_INFINITY);
    }

    @Test(expected=SerializationException.class)
    public void testFloatPositiveInfinity() throws SerializationException {
        JSONSerializer.toString(Float.POSITIVE_INFINITY);
    }

    @Test(expected=SerializationException.class)
    public void testDoubleNaN() throws SerializationException {
        JSONSerializer.toString(Double.NaN);
    }

    @Test(expected=SerializationException.class)
    public void testDoubleNegativeInfinity() throws SerializationException {
        JSONSerializer.toString(Double.NEGATIVE_INFINITY);
    }

    @Test(expected=SerializationException.class)
    public void testDoublePositiveInfinityN() throws SerializationException {
        JSONSerializer.toString(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testEquals() throws IOException, SerializationException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        JSONSerializerListener jsonSerializerListener = new JSONSerializerListener() {
            @Override
            public void beginMap(JSONSerializer jsonSerializer, Map<String, ?> value) {
                System.out.println("Begin dictionary: " + value);
            }

            @Override
            public void endMap(JSONSerializer jsonSerializer) {
                System.out.println("End dictionary");
            }

            @Override
            public void readKey(JSONSerializer jsonSerializer, String key) {
                System.out.println("Read key: " + key);
            }

            @Override
            public void beginList(JSONSerializer jsonSerializer, List<?> value) {
                System.out.println("Begin sequence: " + value);
            }

            @Override
            public void endList(JSONSerializer jsonSerializer) {
                System.out.println("End sequence");
            }

            @Override
            public void readString(JSONSerializer jsonSerializer, String value) {
                System.out.println("Read string: " + value);
            }

            @Override
            public void readNumber(JSONSerializer jsonSerializer, Number value) {
                System.out.println("Read number: " + value);
            }

            @Override
            public void readBoolean(JSONSerializer jsonSerializer, Boolean value) {
                System.out.println("Read boolean: " + value);
            }

            @Override
            public void readNull(JSONSerializer jsonSerializer) {
                System.out.println("Read null");
            }
        };

        jsonSerializer.getJSONSerializerListeners().add(jsonSerializerListener);
        Object o1 = jsonSerializer.readObject(getClass().getResourceAsStream("map.json"));

        jsonSerializer.getJSONSerializerListeners().remove(jsonSerializerListener);
        Object o2 = jsonSerializer.readObject(getClass().getResourceAsStream("map.json"));

        assertTrue(o1.equals(o2));

        List<?> d = BeanAdapter.get(o1, "d");
        d.remove(0);

        assertFalse(o1.equals(o2));
    }
}
