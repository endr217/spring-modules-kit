package dev.moduleskit.modules.media.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class SvgConversionUtil {

    public static void generatePngThumbnail(InputStream svgInput, Path outputPath, int width, int height) throws IOException {
        try {
            TranscoderInput input = new TranscoderInput(svgInput);
            try (var os = java.nio.file.Files.newOutputStream(outputPath)) {
                TranscoderOutput output = new TranscoderOutput(os);
                PNGTranscoder transcoder = new PNGTranscoder();
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
                transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
                transcoder.transcode(input, output);
            }
        } catch (Exception e) {
            throw new IOException("Failed to generate SVG thumbnail", e);
        }
    }
}

