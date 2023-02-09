package org.waldreg.controller.board.reaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.board.dto.ReactionRequestDto;
import org.waldreg.board.reaction.management.ReactionManager;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.user.management.UserManager;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class ReactionController{

    private final ReactionManager reactionManager;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;

    private final UserManager userManager;

    @Autowired
    public ReactionController(ReactionManager reactionManager, DecryptedTokenContextGetter decryptedTokenContextGetter, UserManager userManager){
        this.reactionManager = reactionManager;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
        this.userManager = userManager;
    }

    @Authenticating
    @PermissionVerifying("Reaction manager")
    @GetMapping("/reaction/{board-id}")
    public void reaction(@PathVariable("board-id") int boardId, @RequestParam("reaction-type") String type){
        int id = decryptedTokenContextGetter.get();
        String userId = userManager.readUserById(id).getUserId();
        ReactionRequestDto reactionRequestDto = ReactionRequestDto.builder()
                .boardId(boardId)
                .reactionType(type)
                .userId(userId)
                .build();
        reactionManager.reactionRequest(reactionRequestDto);
    }


}
