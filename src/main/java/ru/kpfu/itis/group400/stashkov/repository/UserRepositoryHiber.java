//package ru.kpfu.itis.group400.stashkov.repository;
//
//import org.hibernate.SessionFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import ru.kpfu.itis.group400.stashkov.model.User;
//
//import java.util.List;
//
//@Repository
//public class UserRepositoryHiber {
//
//    private final SessionFactory sessionFactory;
//
//    // Явно указываем, какой SessionFactory использовать
//    public UserRepositoryHiber(@Qualifier("localSessionFactoryBean") SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    @Transactional("hibernateTransactionManager") // указываем нужный менеджер транзакций
//    public List<User> getAll() {
//        return sessionFactory.getCurrentSession()
//                .createQuery("from User", User.class)
//                .list();
//    }
//}