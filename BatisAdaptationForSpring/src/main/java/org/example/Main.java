package org.example;

import org.example.config.SpringConfig;
import org.example.core.context.AnnotationApplicationContext;
import org.example.core.context.ApplicationContext;
import org.example.entity.User;
import org.example.mapper.TestMapper;

public class Main {
    public static void main(String[] args) {

        ApplicationContext applicationContext = new AnnotationApplicationContext(SpringConfig.class);
        TestMapper testMapper = (TestMapper) applicationContext.getBean("TestMapper");
        User user = testMapper.getUserById(1);
        System.out.println(user);
    }
}
//适配模块（BatisAdaptationForSpring）建议
//
//  - 配置装配 Bean：写一个 @Component/@Configuration（例如 SuHanMyBatisAutoConfiguration），把 Spring 中的数据源、别
//    名、mapper XML 路径转换成 org.example.core.confguration.Configuration，再调用
//    SqlSessionFactoryBuilder.build(Configuration) 生成 SqlSessionFactory（参考 SuHanBatis/.../
//    SqlSessionFactoryBuilder.java:34）。通过 @Autowired 注入数据源等，让 Configuration 在
//    DefaultListableBeanFactory.preInstantiateSingletons 之前准备好。
//  - SqlSessionFactoryBean & SqlSessionTemplate：实现一个 InitializingBean，在 afterPropertiesSet 中用注入的配置构建
//    SqlSessionFactory 并暴露为 Spring Bean；再提供一个模板类统一管理 openSession/commit/close，业务层只需注入模板即可
//    （参考 SuHanBatis/.../DefaultSqlSession.java:160-217）。
//  - Mapper 注册流水线：新增 @Mapper 或 @MapperScan 注解，并让它本身带上 @Component，使得已有的扫描器能把接口注册到容
//    器。编写 MapperProxyBeanPostProcessor（实现 InstantiationAwareBeanPostProcessor），在
//    postProcessBeforeInstantiation 中判断接口是否带 @Mapper，若是则直接返回 sqlSessionTemplate.getMapper(beanClass)。
//    通过 @EnableSuHanBatis 注解 @Import 该处理器，沿用你对 @EnableAspectJAutoProxy 的模式。
//  - 资源元数据同步：在自动配置中暴露属性（mapper XML 列表、别名包等），按启动顺序调用 Configuration.addMapper(...)、
//    addMappers(...) 等（SuHanBatis/src/main/java/org/example/core/confguration/Configuration.java:28 之后的方法）。
//  - 事务/异常衔接：可以先简单封装你已有的 JdbcTransaction 提供 begin/commit/rollback，后续再考虑像 Spring
//    DataAccessException 那样的异常体系，保证容器里捕获出来的异常一致。
//
//  后续动作
//
//  1. 先实现 @EnableSuHanBatis、@Mapper 和 MapperProxyBeanPostProcessor，保证业务代码能直接 @Autowired mapper 接口。
//  2. 完成 SqlSessionFactoryBean + SqlSessionTemplate，在适配模块中把数据源/配置注入并暴露成可被容器管理的 Bean。
//  3. 整理一个配置类或属性 Bean，描述 mapper XML、别名、数据源等输入，再考虑将事务管理、异常转换融入 Spring 的生命周
//     期。