/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;
import java.util.Map;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Course {

  @Property
  private final String courseID;

  @Property
  private final Map<String, String> courseMap;

  public String getCourseID() {
    return courseID;
  }

  public Map<String, String> getCourseMap() {
    return courseMap;
  }

  public Course(@JsonProperty("courseID") final String courseID,
      @JsonProperty("courseMap") final Map<String, String> courseMap) {
    this.courseID = courseID;
    this.courseMap = courseMap;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    Course other = (Course) obj;

    return Objects.equals(getCourseID(), other.getCourseID()) && Objects.equals(getCourseMap(), other.getCourseMap());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCourseID(), getCourseMap());
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
        + " [courseID=" + courseID + ", courseMap=" + courseMap + "]";
  }
}
