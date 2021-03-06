/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.hadoop;

import alluxio.Configuration;
import alluxio.ProjectConstants;
import alluxio.PropertyKey;
import alluxio.client.file.FileSystemContext;
import alluxio.client.lineage.LineageContext;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.reflect.Whitebox;

import java.net.URL;

/**
 * Utility methods for the Hadoop client tests.
 */
public final class HadoopClientTestUtils {
  /**
   * Resets the client to its initial state, re-initializing Alluxio and Hadoop contexts. Resets the
   * initialized flag in {@link AbstractFileSystem} allowing FileSystems with different URIs to be
   * initialized.
   *
   * This method should only be used as a cleanup mechanism between tests.
   */
  public static void resetClient() {
    try {
      Configuration.set(PropertyKey.USER_METRICS_COLLECTION_ENABLED, false);
      FileSystemContext.get().reset(Configuration.global());
      LineageContext.INSTANCE.reset();
      Whitebox.setInternalState(AbstractFileSystem.class, "sInitialized", false);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return true if the hadoop version is 1.x
   */
  public static boolean isHadoop1x() {
    return getHadoopVersion().startsWith("1");
  }

  /**
   * @return true if the hadoop version is 2.x
   */
  public static boolean isHadoop2x() {
    return getHadoopVersion().startsWith("2");
  }

  /**
   * @return the hadoop version
   */
  public static String getHadoopVersion() {
    return ProjectConstants.HADOOP_VERSION;
  }

  private static URL getSourcePath(Class<?> clazz) {
    try {
      clazz = getClassLoader(clazz).loadClass(clazz.getName());
      return clazz.getProtectionDomain().getCodeSource().getLocation();
    } catch (ClassNotFoundException e) {
      throw new AssertionError("Unable to find class " + clazz.getName());
    }
  }

  private static ClassLoader getClassLoader(Class<?> clazz) {
    // Power Mock makes this hard, so try to hack it
    ClassLoader cl = clazz.getClassLoader();
    if (cl instanceof MockClassLoader) {
      cl = cl.getParent();
    }
    return cl;
  }
}
