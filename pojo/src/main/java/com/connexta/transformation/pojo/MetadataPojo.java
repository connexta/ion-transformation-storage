/**
 * Copyright (c) Connexta
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package com.connexta.transformation.pojo;

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.TransformationStatus;
import com.connexta.transformation.pojo.unknown.UnknownMetadataPojo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * This class provides a pojo implementation for the information and state associated with a
 * particular metadata that is being generated for a given transformation capable of reloading all
 * supported fields for all supported versions from persistence. It also provides the capability of
 * persisting back the fields based on the latest version format.
 */
@JsonPropertyOrder({
  "clazz",
  "id",
  "version",
  "transform_id",
  "type",
  "request_info",
  "state",
  "start_time",
  "completion_time",
  "content_type",
  "content_length",
  "failure_reason",
  "failure_message"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(
    use = Id.NAME,
    include = As.PROPERTY,
    property = "clazz",
    defaultImpl = UnknownMetadataPojo.class)
@JsonSubTypes(@Type(MetadataPojo.class))
@JsonTypeName("metadata")
public class MetadataPojo extends Pojo<MetadataPojo> {
  /**
   * Current version format.
   *
   * <p>Version history:
   *
   * <ul>
   *   <li>1 - initial version.
   * </ul>
   */
  public static final int CURRENT_VERSION = 1;

  /** The oldest version supported by the current code (anything before that will fail). */
  public static final int MINIMUM_VERSION = 1;

  @JsonProperty("transform_id")
  @Nullable
  private String transformId;

  @JsonProperty("type")
  @Nullable
  private String type;

  @JsonProperty("request_info")
  @Nullable
  private RequestInfoPojo requestInfo;

  @JsonProperty("state")
  @Nullable
  private String state;

  @JsonProperty("failure_reason")
  @Nullable
  private String failureReason;

  @JsonProperty("failure_message")
  @Nullable
  private String failureMessage;

  @JsonProperty("start_time")
  @Nullable
  private Instant startTime;

  @JsonProperty("completion_time")
  @Nullable
  private Instant completionTime;

  @JsonProperty("content_type")
  @Nullable
  private String contentType;

  @JsonProperty("content_length")
  private long contentLength = -1L;

  /**
   * Gets the identifier of the transformation that this metadata is associated with.
   *
   * @return the id of the transformation that this metadata is associated with or <code>null</code>
   *     if none defined
   */
  @Nullable
  public String getTransformId() {
    return transformId;
  }

  /**
   * Sets the identifier of the transformation that this metadata is associated with.
   *
   * @param transformId the id of the transformation that this metadata is associated with
   * @return this for chaining
   */
  public MetadataPojo setTransformId(@Nullable String transformId) {
    this.transformId = transformId;
    return this;
  }

  /**
   * Gets the type of metadata this is.
   *
   * @return the type of metadata this is or <code>null</code> if none defined
   */
  @Nullable
  public String getType() {
    return type;
  }

  /**
   * Sets the type of metadata this is.
   *
   * @param type the type of metadata this is
   * @return this for chaining
   */
  public MetadataPojo setType(@Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets information about the request that started this metadata transformation.
   *
   * @return information about the request that started this metadata transformation or <code>null
   *     </code> if none defined
   */
  @Nullable
  public RequestInfoPojo getRequestInfo() {
    return requestInfo;
  }

  /**
   * Gets information about the request that started this metadata transformation.
   *
   * @param requestInfo information about the request that started this metadata transformation
   * @return this for chaining
   */
  @Nullable
  public MetadataPojo setRequestInfo(RequestInfoPojo requestInfo) {
    this.requestInfo = requestInfo;
    return this;
  }

  /**
   * Gets the start time of the metadata transformation.
   *
   * @return the time the metadata transformation started or <code>null</code> if none defined
   */
  @Nullable
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Gets the start time of the metadata transformation.
   *
   * @param startTime the time the metadata transformation started
   * @return this for chaining
   */
  @Nullable
  public MetadataPojo setStartTime(Instant startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * Gets the completion time of the metadata transformation.
   *
   * @return the time the metadata transformation completed or <code>null</code> if it is still in
   *     progress
   */
  @Nullable
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * Gets the completion time of the metadata transformation.
   *
   * @param completionTime the time the metadata transformation completed or <code>null</code> if it
   *     is still in progress
   */
  @Nullable
  public MetadataPojo setCompletionTime(@Nullable Instant completionTime) {
    this.completionTime = completionTime;
    return this;
  }

  /**
   * Gets the current state of the metadata transformation.
   *
   * @return the current metadata transformation state or <code>null</code> if none defined
   */
  @Nullable
  public String getState() {
    return state;
  }

  /**
   * Sets the current state of the metadata transformation.
   *
   * @param state the current metadata transformation state
   * @return this for chaining
   */
  public MetadataPojo setState(String state) {
    this.state = state;
    return this;
  }

  /**
   * Sets the current state of the metadata transformation.
   *
   * @param state the current metadata transformation state
   * @return this for chaining
   */
  public MetadataPojo setState(TransformationStatus.State state) {
    this.state = state.name();
    return this;
  }

  /**
   * Gets the reason this metadata failed to be created.
   *
   * @return the failure reason or <code>null</code> if the metadata hasn't been generated or failed
   *     to be successful. This could mean the metadata is still being generated or was generated
   *     successfully
   */
  @Nullable
  public String getFailureReason() {
    return failureReason;
  }

  /**
   * Sets the reason this metadata failed to be created.
   *
   * @param failureReason the failure reason or <code>null</code> if the metadata hasn't been
   *     generated or failed to be successful. This could mean the metadata is still being generated
   *     or was generated successfully
   * @return this for chaining
   */
  public MetadataPojo setFailureReason(@Nullable String failureReason) {
    this.failureReason = failureReason;
    return this;
  }

  /**
   * Sets the reason this metadata failed to be created.
   *
   * @param failureReason the failure reason or <code>null</code> if the metadata hasn't been
   *     generated or failed to be successful. This could mean the metadata is still being generated
   *     or was generated successfully
   * @return this for chaining
   */
  public MetadataPojo setFailureReason(@Nullable ErrorCode failureReason) {
    this.failureReason = (failureReason != null) ? failureReason.name() : null;
    return this;
  }

  /**
   * Gets a message providing more information as to why this metadata failed to be created.
   *
   * @return a message indicating why this metadata failed to be created or <code>null</code> if no
   *     message is available. This could mean the metadata is still being generated or was
   *     generated successfully
   */
  @Nullable
  public String getFailureMessage() {
    return failureMessage;
  }

  /**
   * Sets a message providing more information as to why this metadata failed to be created.
   *
   * @param failureMessage a message indicating why this metadata failed to be created or <code>null
   *     </code> if no message is available. This could mean the metadata is still being generated
   *     or was generated successfully
   * @return this for chaining
   */
  public MetadataPojo setFailureMessage(@Nullable String failureMessage) {
    this.failureMessage = failureMessage;
    return this;
  }

  /**
   * Gets the content type for the metadata file generated.
   *
   * @return the content type for the metadata file generated or <code>null</code> if none generated
   */
  @Nullable
  public String getContentType() {
    return contentType;
  }

  /**
   * Sets the content type for the metadata file generated.
   *
   * @param contentType the content type for the metadata file generated
   * @return this for chaining
   */
  public MetadataPojo setContentType(@Nullable String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * Gets the length for the metadata file generated.
   *
   * @return the length for the metadata file generated or <code>-1L</code> if none generated
   */
  @Nullable
  public long getContentLength() {
    return contentLength;
  }

  /**
   * Sets the length for the metadata file generated.
   *
   * @param contentLength the length for the metadata file generated
   * @return this for chaining
   */
  public MetadataPojo setContentLength(long contentLength) {
    this.contentLength = contentLength;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        type,
        transformId,
        type,
        state,
        failureReason,
        failureMessage,
        startTime,
        completionTime,
        requestInfo,
        contentType,
        contentLength);
  }

  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj) && (obj instanceof MetadataPojo)) {
      final MetadataPojo pojo = (MetadataPojo) obj;

      return (contentLength == pojo.contentLength)
          && Objects.equals(transformId, pojo.transformId)
          && Objects.equals(type, pojo.type)
          && Objects.equals(requestInfo, pojo.requestInfo)
          && Objects.equals(state, pojo.state)
          && Objects.equals(failureReason, pojo.failureReason)
          && Objects.equals(failureMessage, pojo.failureMessage)
          && Objects.equals(startTime, pojo.startTime)
          && Objects.equals(completionTime, pojo.completionTime)
          && Objects.equals(contentType, pojo.contentType);
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format(
        "MetadataPojo[id=%s, version=%d, transformId=%s, type=%s, requestInfo=%s, state=%s, startTime=%s, completionTime=%s, contentType=%s, contentLength=%d, failureReason=%s, failureMessage=%s]",
        getId(),
        getVersion(),
        transformId,
        type,
        requestInfo,
        state,
        startTime,
        completionTime,
        contentType,
        contentLength,
        failureReason,
        failureMessage);
  }
}
