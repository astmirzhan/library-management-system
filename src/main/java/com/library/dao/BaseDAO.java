package com.library.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Generic interface for Data Access Objects.
 * Defines the standard CRUD operations.
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's primary key
 */
public interface BaseDAO<T, ID> {

    /**
     * Saves a new entity to the database.
     *
     * @param entity the entity to save
     * @return the saved entity with generated ID
     * @throws SQLException if a database error occurs
     */
    T save(T entity) throws SQLException;

    /**
     * Finds an entity by its primary key.
     *
     * @param id the primary key
     * @return Optional containing the entity if found, empty otherwise
     * @throws SQLException if a database error occurs
     */
    Optional<T> findById(ID id) throws SQLException;

    /**
     * Returns all entities from the database.
     *
     * @return list of all entities
     * @throws SQLException if a database error occurs
     */
    List<T> findAll() throws SQLException;

    /**
     * Updates an existing entity.
     *
     * @param entity the entity to update
     * @return true if updated successfully, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean update(T entity) throws SQLException;

    /**
     * Deletes an entity by its primary key.
     *
     * @param id the primary key
     * @return true if deleted successfully, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean deleteById(ID id) throws SQLException;
}