package com.hqy.cloud.auth.service.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.hqy.cloud.account.request.AccountQueryParams;
import com.hqy.cloud.account.request.RegistryAccountByPhoneParams;
import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.account.response.AccountResultCode;
import com.hqy.cloud.account.response.RegisterInfo;
import com.hqy.cloud.account.service.AccountFacadeService;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.entity.convertor.AccountConvertor;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.base.AccountConstants;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import com.hqy.cloud.rpc.dubbo.facade.Facade;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
@RequiredArgsConstructor
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class AccountFacadeServiceImpl implements AccountFacadeService {
    private final PasswordEncoder passwordEncoder;
    private final AccountDomainService accountDomainService;
    private final AccountOperationService accountOperationService;
    private final RandomCodeService randomCodeService;

    @Facade
    @Override
    public R<AccountInfo> query(AccountQueryParams queryParams) {
        Account account = null;
        Long id = queryParams.getId();
        if (id != null) {
            account = accountDomainService.findById(id);
        } else if (StringUtils.isNotBlank(queryParams.getPhone())) {
            account = accountDomainService.queryAccountByUniqueIndex(queryParams.getPhone());
        } else if (StringUtils.isNotBlank(queryParams.getEmail())) {
            account = accountDomainService.queryAccountByUniqueIndex(queryParams.getEmail());
        }
        if (account == null) {
            return R.failed(AccountResultCode.USER_NOT_FOUND);
        }

        AccountInfo accountInfo = AccountConvertor.CONVERTOR.mapToVo(account);
        return R.ok(accountInfo);
    }

    @Facade
    @Override
    public R<List<AccountInfo>> queryList(Collection<Long> ids) {
        List<Account> accounts = accountDomainService.findByIds(ids);
        return R.ok(accounts.stream().map(AccountConvertor.CONVERTOR::mapToVo).toList());
    }

    @Facade
    @Override
    public R<RegisterInfo> registerByPhone(RegistryAccountByPhoneParams registryParams) {
        String phone = registryParams.getPhone();
        String code = registryParams.getCode();
        // 检查一下验证码是否正确.
        if (!randomCodeService.isExist(code, phone, RandomCodeScene.SMS_AUTH)) {
            // 验证码错误
            return R.failed(AccountResultCode.VERIFY_CODE_ERROR);
        }
        String username = StringUtils.isNotBlank(registryParams.getUsername()) ? registryParams.getUsername() :
                AccountConstants.DEFAULT_NICKNAME_PREFIX + DateUtil.format(new Date(), "MMddHHmm") + phone.substring(7, 11);
        String password = StringUtils.isNotBlank(registryParams.getPassword()) ? passwordEncoder.encode(registryParams.getPassword()) :
                passwordEncoder.encode(RandomUtil.randomString(8));
        Account account = Account.register(username, password, null, phone, UserRole.CUSTOMER, null);
        AccountProfile profile = AccountProfile.register(account.getId(), null, username, phone, registryParams.getAvatar());
        boolean result = accountOperationService.registryAccount(account, profile);
        if (result) {
            // 异步对验证进行续期.防止验证码过期无法登录
            Thread.ofVirtual().start(() -> randomCodeService.saveCode(code, phone, RandomCodeScene.SMS_AUTH));
        }
        return result ? R.ok(new RegisterInfo(account.getId(), account.getUsername(), profile.getNickname(), profile.getAvatar())) : R.failed();
    }
}
