package com.udemy.app.ws.io.repository;

import com.udemy.app.ws.io.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

   RoleEntity findByName(String name);
}
