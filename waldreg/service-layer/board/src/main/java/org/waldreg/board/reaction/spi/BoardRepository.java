package org.waldreg.board.reaction.spi;

import org.waldreg.board.dto.ReactionDto;

public interface BoardRepository{

    void addReaction(ReactionDto reactionDto);

    void deleteReaction(ReactionDto reactionDto);

}
