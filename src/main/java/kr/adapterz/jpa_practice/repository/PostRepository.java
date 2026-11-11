package kr.adapterz.jpa_practice.repository;

import kr.adapterz.jpa_practice.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByIdDesc(); //최신 게시글이 위에 오도록 ID 내림차순 정렬
}
