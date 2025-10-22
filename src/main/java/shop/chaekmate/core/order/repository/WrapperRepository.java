package shop.chaekmate.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.Wrapper;

public interface WrapperRepository extends JpaRepository<Wrapper, Long> {
    boolean existByNameAndDeletedAtNull(String name);
}
