package com.company.backend.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByStatus(ItemStatus status, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
