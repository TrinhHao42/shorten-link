package iuh.fit.se.shortenlink.repository;

import iuh.fit.se.shortenlink.entity.Link;
import iuh.fit.se.shortenlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findBySlug(String slug);

    List<Link> findByUser(User user);

    boolean existsBySlug(String slug);
}

