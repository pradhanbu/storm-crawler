/**
 * Licensed to DigitalPebble Ltd under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * DigitalPebble licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpebble.storm.crawler.parse.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

import com.digitalpebble.storm.crawler.bolt.ParserBolt;

public class ParsingTester {
    protected ParserBolt bolt;
    protected TestOutputCollector output;

    @Before
    public void setupParserBolt() {
        bolt = new ParserBolt();
        output = new TestOutputCollector();
    }

    @After
    public void cleanupBolt() {
        if (bolt != null) {
            bolt.cleanup();
        }
        output = null;
    }

    protected void prepareParserBolt(String configFile) {
        Map parserConfig = new HashMap();
        parserConfig.put("parsefilters.config.file", configFile);
        bolt.prepare(parserConfig, TestUtil.getMockedTopologyContext(),
                new OutputCollector(output));
    }

    protected void parse(String url, String filename) throws IOException {
        parse(url, filename, new HashMap<String, String[]>());
    }

    protected void parse(String url, String filename,
            Map<String, String[]> metadata) throws IOException {
        byte[] content = readContent(filename);
        Tuple tuple = mock(Tuple.class);
        when(tuple.getBinaryByField("content")).thenReturn(content);
        when(tuple.getStringByField("url")).thenReturn(url);
        when(tuple.getValueByField("metadata")).thenReturn(metadata);
        bolt.execute(tuple);
    }

    protected byte[] readContent(String filename) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getClassLoader().getResourceAsStream(filename),
                baos);
        return baos.toByteArray();
    }

}
