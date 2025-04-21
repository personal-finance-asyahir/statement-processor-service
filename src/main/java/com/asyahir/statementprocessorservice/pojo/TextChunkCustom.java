package com.asyahir.statementprocessorservice.pojo;

import org.apache.commons.collections4.CollectionUtils;
import technology.tabula.TextChunk;
import technology.tabula.TextElement;

import java.text.Normalizer;
public class TextChunkCustom extends TextChunk {

    public TextChunkCustom(TextChunk textChunk) {
        super(textChunk.getTextElements());
    }

    @Override
    public String getText() {
        if (CollectionUtils.size(this.textElements) == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for(TextElement te : this.textElements) {
                sb.append(te.getText());
            }
            return Normalizer.normalize(sb.toString(), Normalizer.Form.NFKC);
        }
    }
}
