package org.waldreg.board.reaction.spi;

import org.waldreg.board.dto.ReactionDto;

public interface ReactionInBoardRepository{

    ReactionDto getReactionDto(int boardId);

    void storeReactionDto(ReactionDto reactionDto);

    boolean isExistBoard(int boardId);

}
