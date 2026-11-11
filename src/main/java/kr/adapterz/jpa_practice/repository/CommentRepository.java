package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findAllByPostIdOrderByIdAsc(Long postId);
}
