package com.chaekdojang.api.domain.commerce;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseLinkRepository extends JpaRepository<PurchaseLink, Long> {

    List<PurchaseLink> findAllByBookId(Long bookId);
}
