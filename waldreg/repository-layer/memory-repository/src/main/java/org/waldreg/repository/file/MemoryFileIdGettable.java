package org.waldreg.repository.file;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import org.waldreg.board.file.spi.FileIdGettable;

@Service
public class MemoryFileIdGettable implements FileIdGettable{

    private final AtomicInteger atomicInteger;

    {
        atomicInteger = new AtomicInteger(1);
    }

    @Override
    public int getAndIncreaseMaxFileId(){
        return atomicInteger.getAndIncrement();
    }

}
