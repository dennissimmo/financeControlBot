package com.denchik.demo.repository;

import com.denchik.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface OperationRepository extends JpaRepository<Operation,Long> {
    List<Operation> findAllByCategory (Category category);
    List<Operation> findOperationByUserAndCategoryNotNull(User user);
    List<Operation> findOperationByTypeOperationAndUserOrderByAmount(TypeOperation typeOperation, User user);
    List<Operation> findTop5ByOrderByCreateAtDesc ();
    @Query(value = "SELECT o. * FROM operation o WHERE o.user_id = ?1 AND date_part('month',create_at) = ?3 AND date_part('year',create_at) = ?4 and o.category_id IS NOT NULL ORDER BY o.create_at DESC limit ?2",nativeQuery = true)
    List<Operation> findLastUserOperations (int user_id,int limit,int month, int year);
    Operation findTopByOrderByCreateAt ();
    Operation findTopByOrderById ();
    Operation findById (Integer id);
    @Query(value = "SELECT DISTINCT c.name, sum(amount) FROM operation LEFT JOIN category c on c.id = operation.category_id LEFT JOIN users u on u.id = operation.user_id LEFT JOIN type_operation t on t.id = operation.type_operation_id WHERE t.name='EXPENSE' AND date_part('month',create_at) =?1 GROUP BY c.name",nativeQuery = true)
    List<Operation> getSumOperationByCategoryPerMonth (int userId);
    /*@Query("SELECT sum(o.amount) FROM Operation o WHERE o.typeOperation= :typeOperation AND o.user= :user")*/
    @Query(value = "SELECT sum(operation.amount) FROM operation WHERE operation.type_operation_id = ?1 AND operation.user_id= ?2 AND date_part('month',operation.create_at) = ?3 AND date_part('year',operation.create_at) = ?4",nativeQuery = true)
    double sumAmountOperationsForOperationType (TypeOperation typeOperation,User user,int month,int year);
    List<Operation> findOperationsByCategory (Category category);
    @Query("SELECT sum(o.amount) FROM Operation o WHERE o.category= :category AND o.user= :user")
    double sumAmountOperationsForCategory (Category category,User user);
    @Query("SELECT c FROM Category c INNER join fetch c.typeCategory as typeCategory where typeCategory.name_type= :name_type ")
    List<Category> findCategoriesByTypeCategoryByName (@Param("name_type") String name_type);
    @Query(value = "SELECT * FROM Operation WHERE DAY((DATE)create_at) = 4 GROUP BY DAY(create_at)",nativeQuery = true)
    List<Operation> findOperationByDate ();
    //@Query(value = "SELECT * FROM Operation WHERE date_part('day',create_at) = ?1",nativeQuery = true)
    // @Query(value = "SELECT date_part('month',create_at)",nativeQuery = true)
    @Query(value = "SELECT * FROM Operation WHERE date_part('month',create_at) = ?1 AND user_id=?2 AND date_part('year',create_at) = ?3 ORDER BY create_at DESC",nativeQuery = true)
    List<Operation> getOperationByNumberMonthAndYear (int numberOfMonth,int user_id,int year);
    //@Query(value = "SELECT * FROM Operation WHERE date_part('month',create_at) = 5")
    void deleteAllByUserId (Integer userID);
}
