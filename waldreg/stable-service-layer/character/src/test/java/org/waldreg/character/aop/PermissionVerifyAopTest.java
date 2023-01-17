package org.waldreg.character.aop;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.permission.verification.PermissionVerifier;
import org.waldreg.util.annotation.AnnotationExtractor;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PermissionVerifyAop.class, AnnotationExtractor.class})
public class PermissionVerifyAopTest{

    @Autowired
    private PermissionVerifyAop permissionVerifyAop;
    @MockBean
    private PermissionVerifier permissionVerifier;
    @MockBean
    private DecryptedTokenContextGetter decryptedTokenContextGetter;
    @MockBean
    private CharacterInUserReadable characterInUserReadable;
    @MockBean
    private PermissionVerifierClient permissionVerifierClient;
    private final int successId = 1;
    private final int failId = 2;
    private final String permissionName = "Permission verifier aop test";

    @BeforeEach
    @AfterEach
    public void TEST_SET_UP(){
        setUpSuccessCharacterAndPermission();
        setUpFailCharacterAndPermission();
    }

    private void setUpSuccessCharacterAndPermission(){
        String permissionSuccessStatus = "success";
        Mockito.when(characterInUserReadable.readCharacterByUserId(1)).thenReturn(CharacterDto.builder()
                .characterName("success character")
                .permissionDtoList(List.of(PermissionDto.builder()
                        .name(permissionName)
                        .status(permissionSuccessStatus)
                        .build()))
                .build());
        Mockito.when(permissionVerifier.verify(permissionName, permissionSuccessStatus)).thenReturn(true);
    }

    private void setUpFailCharacterAndPermission(){
        String permissionFailStatus = "fail";
        Mockito.when(characterInUserReadable.readCharacterByUserId(2)).thenReturn(CharacterDto.builder()
                .characterName("fail character")
                .permissionDtoList(List.of(PermissionDto.builder()
                        .name(permissionName)
                        .status(permissionFailStatus)
                        .build()))
                .build());
        Mockito.when(permissionVerifier.verify(permissionName, permissionFailStatus)).thenReturn(false);
    }

    @Test
    @DisplayName("권한 체크 성공 테스트")
    public void PERMISSION_CHECK_SUCCESS_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permissionVerifierClient);
        aspectJProxyFactory.addAspect(permissionVerifyAop);
        PermissionVerifierClient permissionVerifierClient = aspectJProxyFactory.getProxy();

        // when
        Mockito.when(decryptedTokenContextGetter.get()).thenReturn(1);

        // then
        Assertions.assertDoesNotThrow(permissionVerifierClient::method);
    }

    @Test
    @DisplayName("권한 체크 실패 테스트 - 권한 없음")
    public void PERMISSION_CHECK_FAIL_NO_PERMISSION_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permissionVerifierClient);
        aspectJProxyFactory.addAspect(permissionVerifyAop);
        PermissionVerifierClient permissionVerifierClient = aspectJProxyFactory.getProxy();

        // when
        Mockito.when(decryptedTokenContextGetter.get()).thenReturn(2);

        // then
        Assertions.assertThrows(NoPermissionException.class, permissionVerifierClient::method);
    }

    @Test
    @DisplayName("권한 체크 실패 테스트 - 알 수 없는 권한")
    public void PERMISSION_CHECK_FAIL_UNKNOWN_PERMISSION_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permissionVerifierClient);
        aspectJProxyFactory.addAspect(permissionVerifyAop);
        PermissionVerifierClient permissionVerifierClient = aspectJProxyFactory.getProxy();

        // when
        Mockito.when(decryptedTokenContextGetter.get()).thenReturn(1);

        // then
        Assertions.assertThrows(UnknownPermissionException.class, permissionVerifierClient::unknownPermission);
    }

    @Test
    @DisplayName("권한 체크 성공 및 \"PermissionVerifyState\" 파라미터 전달 테스트")
    public void PERMISSION_CHECK_SUCCESS_AND_PASS_PARAMETER_TEST(){
        // given
        Mockito.when(permissionVerifierClient.state(Mockito.any(), Mockito.any(PermissionVerifyState.class))).thenReturn(() -> true);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permissionVerifierClient);
        aspectJProxyFactory.addAspect(permissionVerifyAop);
        PermissionVerifierClient permissionVerifierClient = aspectJProxyFactory.getProxy();

        // when
        Mockito.when(decryptedTokenContextGetter.get()).thenReturn(1);

        // then
        Assertions.assertTrue(permissionVerifierClient.state(null, null).isVerified());
    }

    @Test
    @DisplayName("권한 체크 실패 및 \"PermissionVerifyState\" 파라미터 전달 테스트 - VerifyingFailBehavior.PASS")
    public void PERMISSION_CHECK_FAIL_AND_PASS_PARAMETER_TEST(){
        // given
        Mockito.when(permissionVerifierClient.failState(Mockito.anyInt(), Mockito.any(PermissionVerifyState.class))).thenReturn(() -> false);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permissionVerifierClient);
        aspectJProxyFactory.addAspect(permissionVerifyAop);
        PermissionVerifierClient permissionVerifierClient = aspectJProxyFactory.getProxy();

        // when
        Mockito.when(decryptedTokenContextGetter.get()).thenReturn(2);

        // then
        Assertions.assertFalse(permissionVerifierClient.failState(0, null).isVerified());
    }

}
