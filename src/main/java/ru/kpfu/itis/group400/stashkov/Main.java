//package ru.kpfu.itis.group400.stashkov;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.TransactionStatus;
//import org.springframework.transaction.support.TransactionCallbackWithoutResult;
//import org.springframework.transaction.support.TransactionTemplate;
//import ru.kpfu.itis.group400.stashkov.config.CoreConfig;
//import ru.kpfu.itis.group400.stashkov.config.PersistenceConfig;
//import ru.kpfu.itis.group400.stashkov.model.User;
//import ru.kpfu.itis.group400.stashkov.repository.UserRepository;
//
//public class Main {
//    public static void main(String[] args) {
//        // 1. Запускаем Spring контекст только с PersistenceConfig (без веб-компонентов)
//        ApplicationContext context = new AnnotationConfigApplicationContext(PersistenceConfig.class);
//
//        // 2. Получаем нужные бины
//        UserRepository userRepository = context.getBean(UserRepository.class);
//        PlatformTransactionManager txManager = context.getBean(PlatformTransactionManager.class);
//
//        // 3. Создаём TransactionTemplate для ручного управления транзакциями
//        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
//
//        // 4. Выполняем операции внутри транзакции (методы create/update требуют транзакции)
//        txTemplate.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus status) {
//                // Создание пользователя
//                User user = new User();
//                user.setUsername("manual_user");
//                userRepository.create(user);
//                System.out.println("Создан пользователь с username = manual_user");
//
//                // Проверка создания
//                userRepository.findByUsername("manual_user")
//                        .ifPresentOrElse(
//                                u -> System.out.println("Найден в БД: " + u.getUsername() + ", id=" + u.getId()),
//                                () -> System.out.println("Пользователь не найден!")
//                        );
//
//                // Обновление (нужен ID, получаем сущность из БД)
//                User created = userRepository.findByUsername("manual_user").orElseThrow();
//                created.setUsername("updated_user");
//                userRepository.update(created);
//                System.out.println("Обновили username на updated_user");
//
//                // Проверка обновления
//                userRepository.findByUsername("updated_user")
//                        .ifPresentOrElse(
//                                u -> System.out.println("После обновления найден: " + u.getUsername()),
//                                () -> System.out.println("После обновления не найден!")
//                        );
//            }
//        });
//
//        // Финальная проверка вне транзакции (данные уже закоммичены)
//        System.out.println("\n--- Повторная проверка после транзакции ---");
//        userRepository.findByUsername("updated_user")
//                .ifPresentOrElse(
//                        u -> System.out.println("Итог: пользователь " + u.getUsername() + " с id=" + u.getId()),
//                        () -> System.out.println("Итог: пользователь не найден")
//                );
//
//        // Закрываем контекст (для in-memory БД необязательно)
//        ((AnnotationConfigApplicationContext) context).close();
//    }
//}
