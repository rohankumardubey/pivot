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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pivot.beans.BeanAdapter;
import org.apache.pivot.io.SerializationException;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.util.TypeLiteral;
import org.junit.Test;

public class BindTest {
    /**
     * Tests returning an untyped list.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    public void testUntypedList() throws IOException, SerializationException {
        JSONSerializer listSerializer = new JSONSerializer(ArrayList.class);
        List<?> list = (List<?>)listSerializer.readObject(new StringReader("[1, 2, 3, 4, 5]"));
        assertEquals(list.get(0), 1);
    }

    /**
     * Tests returning a typed list using {@code org.apache.pivot.util.TypeLiteral}.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTypedList() throws IOException, SerializationException {
        JSONSerializer listSerializer = new JSONSerializer();
        List<Object> list =
            (List<Object>)listSerializer.readObject(getClass().getResourceAsStream("list.json"));

        JSONSerializer typedListSerializer =
            new JSONSerializer((new TypeLiteral<ArrayList<SampleBean2>>() {}).getType());
        ArrayList<SampleBean2> typedList =
            (ArrayList<SampleBean2>)typedListSerializer.readObject(getClass().getResourceAsStream("list.json"));

        Object item0 = typedList.get(0);
        assertTrue(item0 instanceof SampleBean2);
        assertEquals(typedList.get(0).getA(), BeanAdapter.get(list, "[0].a"));
    }

    /**
     * Tests returning a subclass of a generic {@code org.apache.pivot.collections.List}.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testListSubclass() throws IOException, SerializationException {
        JSONSerializer listSerializer = new JSONSerializer();
        List<Object> list =
            (List<Object>)listSerializer.readObject(getClass().getResourceAsStream("list.json"));

        JSONSerializer typedListSerializer = new JSONSerializer(SampleBean2ListSubclass.class);
        SampleBean2List typedList =
            (SampleBean2List)typedListSerializer.readObject(getClass().getResourceAsStream("list.json"));

        Object item0 = typedList.get(0);
        assertTrue(item0 instanceof SampleBean2);
        assertEquals(typedList.get(0).getA(), BeanAdapter.get(list, "[0].a"));
    }

    /**
     * Tests returning an untyped map.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUntypedMap() throws IOException, SerializationException {
        JSONSerializer mapSerializer = new JSONSerializer(HashMap.class);
        HashMap<String, ?> map = (HashMap<String, ?>)mapSerializer.readObject(new StringReader("{a:1, b:2, c:'3'}"));
        assertEquals(map.get("a"), 1);
    }

    /**
     * Tests returning a typed map using {@code org.apache.pivot.util.TypeLiteral}.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTypedMap() throws IOException, SerializationException {
        JSONSerializer typedMapSerializer =
            new JSONSerializer((new TypeLiteral<HashMap<String, SampleBean2>>() {}).getType());

        HashMap<String, SampleBean2> map =
            (HashMap<String, SampleBean2>)typedMapSerializer.readObject(new StringReader("{foo: {a:1, b:2, c:'3'}}"));

        assertTrue(BeanAdapter.get(map, "foo") instanceof SampleBean2);
        assertEquals(BeanAdapter.get(map, "foo.c"), "3");
    }

    /**
     * Tests returning a subclass of a generic {@code org.apache.pivot.collections.Map}.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    public void testMapSubclass() throws IOException, SerializationException {
        JSONSerializer typedMapSerializer = new JSONSerializer(SampleBean2MapSubclass.class);

        SampleBean2Map map =
            (SampleBean2Map)typedMapSerializer.readObject(new StringReader("{foo: {a:1, b:2, c:'3'}}"));

        assertTrue(BeanAdapter.get(map, "foo") instanceof SampleBean2);
        assertEquals(BeanAdapter.get(map, "foo.c"), "3");
    }

    /**
     * Tests returning a Java bean value.
     *
     * @throws IOException
     * @throws SerializationException
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testBean() throws IOException, SerializationException {
        JSONSerializer mapSerializer = new JSONSerializer();
        Map<String, Object> map =
            (Map<String, Object>)mapSerializer.readObject(getClass().getResourceAsStream("map.json"));

        JSONSerializer beanSerializer = new JSONSerializer(SampleBean1.class);
        SampleBean1 typedMap =
            (SampleBean1)beanSerializer.readObject(getClass().getResourceAsStream("map.json"));

        assertEquals(typedMap.getA(), BeanAdapter.get(map, "a"));
        assertEquals(typedMap.getB(), BeanAdapter.get(map, "b"));
        assertEquals(typedMap.getC(), BeanAdapter.get(map, "c"));
        assertEquals(typedMap.getD(), BeanAdapter.get(map, "d"));
        assertEquals(typedMap.getE(), BeanAdapter.get(map, "e"));
        assertEquals(typedMap.getI().getA(), BeanAdapter.get(map, "i.a"));

        Object k0 = typedMap.getK().get(0);
        assertTrue(k0 instanceof SampleBean2);
        assertEquals(typedMap.getK().get(0).getA(), BeanAdapter.get(map, "k[0].a"));
    }
}
