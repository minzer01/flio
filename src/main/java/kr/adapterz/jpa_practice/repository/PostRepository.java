package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.writer left join fetch p.themes order by p.id desc")
    List<Post> findAllWithWriter();
}
