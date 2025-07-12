package iuh.fit.se.shortenlink.repository;

import iuh.fit.se.shortenlink.entity.FileObject;
import iuh.fit.se.shortenlink.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    Optional<FileObject> findByLink(Link link);

    void deleteByLink(Link link);
}

