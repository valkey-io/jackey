package io.valkey.graph;

/**
 * Store a local cache in the client, for a specific graph. Holds the labels, property names and
 * relationship types.
 * @deprecated Redis Graph support is deprecated.
 */
@Deprecated
interface GraphCache {

  /**
   * @param index - index of label
   * @return requested label
   */
  String getLabel(int index);

  /**
   * @param index index of the relationship type
   * @return requested relationship type
   */
  String getRelationshipType(int index);

  /**
   * @param index index of property name
   * @return requested property
   */
  String getPropertyName(int index);
}
