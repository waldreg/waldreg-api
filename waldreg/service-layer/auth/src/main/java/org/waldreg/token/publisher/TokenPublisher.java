package org.waldreg.token.publisher;

import org.waldreg.token.dto.TokenDto;

public interface TokenPublisher{

    String publish(TokenDto tokenDto);

}
