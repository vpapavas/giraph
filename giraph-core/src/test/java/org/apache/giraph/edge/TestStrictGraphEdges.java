/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.giraph.edge;

import com.google.common.collect.Lists;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.apache.giraph.graph.TestVertexAndEdges.instantiateOutEdges;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link OutEdges} implementations that disallow parallel edges.
 */
public class TestStrictGraphEdges {
  /** {@link OutEdges} classes to be tested. */
  private Collection<Class<? extends OutEdges>> edgesClasses =
      Lists.newArrayList();

  @Before
  public void setUp() {
    edgesClasses.add(HashMapEdges.class);
    edgesClasses.add(LongDoubleHashMapEdges.class);
  }

  /**
   * Ensures that all strict graph {@link OutEdges} implementations
   * disallow parallel edges.
   */
  @Test
  public void testParallelEdges() {
    for (Class<? extends OutEdges> edgesClass : edgesClasses) {
      testParallelEdgesClass(edgesClass);
    }
  }

  private void testParallelEdgesClass(
      Class<? extends OutEdges> edgesClass) {
    OutEdges<LongWritable, DoubleWritable> edges =
        instantiateOutEdges(edgesClass);

    // Initial edges list contains parallel edges.
    List<Edge<LongWritable, DoubleWritable>> initialEdges = Lists.newArrayList(
        EdgeFactory.create(new LongWritable(1), new DoubleWritable(1)),
        EdgeFactory.create(new LongWritable(2), new DoubleWritable(2)),
        EdgeFactory.create(new LongWritable(3), new DoubleWritable(3)),
        EdgeFactory.create(new LongWritable(2), new DoubleWritable(20)));

    edges.initialize(initialEdges);

    // Only one of the two parallel edges should be left.
    assertEquals(3, edges.size());

    // Adding a parallel edge shouldn't change the number of edges.
    edges.add(EdgeFactory.create(new LongWritable(3), new DoubleWritable(30)));
    assertEquals(3, edges.size());
  }
}
