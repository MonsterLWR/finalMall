package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能描述: DAO接口
 *
 * @auther: Lee
 * @date: 2018/9/16 10:26
 */
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 功能描述: 检查该用户名是否存在
     *
     * @param: username
     * @return: int:该用户名存在的个数
     * @auther: Lee
     * @date: 2018/9/16 10:30
     */
    int checkUsername(String username);

    /**
     * 功能描述: 检查该email是否存在
     *
     * @param: email
     * @return: int
     * @auther: Lee
     * @date: 2018/9/16 11:44
     */
    int checkEmail(String email);

    /**
     * 功能描述: 检查username和password是否对应，返回0说明密码错误。
     *
     * @param: username,password
     * @return: User
     * @auther: Lee
     * @date: 2018/9/16 10:32
     */
    User selectLogin(@Param("username") String username, @Param("password") String password);

    /**
     * 功能描述: 根据username获取密码找回问题
     *
     * @param: username
     * @return: String
     * @auther: Lee
     * @date: 2018/9/16 10:52
     */
    String selectQuestionByUsername(String username);

    /**
     * 功能描述: 判断数据库中的username与对应的密码找回问题和答案是否匹配
     *
     * @param: username，question，answer
     * @return: int
     * @auther: Lee
     * @date: 2018/9/16 11:05
     */
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    /**
     * 功能描述: 更新数据库中username的password
     *
     * @param: username,passwordNew
     * @return: int
     * @auther: Lee
     * @date: 2018/9/16 11:07
     */
    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    /**
     * 功能描述: 检查user_id和password是否匹配
     *
     * @param: password,userId
     * @return:
     * @auther: Lee
     * @date: 2018/9/16 11:11
     */
    int checkPassword(@Param("password") String password, @Param("userId") int userId);

    /**
     * 功能描述: 检查email除该userid对应的记录外无重复。
     *
     * @param: userId，email
     * @return: int
     * @auther: Lee
     * @date: 2018/9/16 11:43
     */
    int checkEmailByUserId(@Param("userId")int userId, @Param("email")String email);


    /**
     * 功能描述: 选择用户
     *
     * @param:
     * @return:
     * @auther: Lee
     * @date: 2018/9/25 19:36
     */
    List<User> selectUsers();

    /**
     * 功能描述: 用户总数
     *
     * @param:
     * @return:
     * @auther: Lee
     * @date: 2018/9/25 20:25
     */
    Integer selectCount();
}