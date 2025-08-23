package resourceservice.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import resourceservice.validation.TagValidation;

import java.io.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static resourceservice.util.AudioTagUtils.*;

public class TagValidationImpl implements ConstraintValidator<TagValidation, byte[]> {

    @Override
    public void initialize(TagValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(byte[] bytes, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return areTagsValid(bytes);
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean areTagsValid(byte[] songData) throws IOException, TikaException, SAXException {
        try (InputStream inputstream = new ByteArrayInputStream(songData)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();
            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputstream, handler, metadata, pcontext);
            return isNotBlank(metadata.get(TITLE_TAG)) && isNotBlank(metadata.get(ARTIST_TAG))
                    && isNotBlank(metadata.get(ALBUM_TAG)) && isNotBlank(metadata.get(RELEASE_DATE_TAG));
        }
    }
}
