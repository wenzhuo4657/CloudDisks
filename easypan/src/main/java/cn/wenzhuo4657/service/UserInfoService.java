package cn.wenzhuo4657.service;

import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.domain.query.UserInfoQuery;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * (UserInfo)表服务接口
 *
 * @author makejava
 * @since 2024-03-15 14:18:12
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(String email, String nickName, String password, String emailCode);

    SessionDto login(String email, String password);


    void resetPwd(String email, String password, String emailCode);

    PaginationResultDto findListByPage(UserInfoQuery query);
}
