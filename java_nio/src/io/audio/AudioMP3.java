package io.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioMP3 {

    private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    private static void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            int count = 0;
            while (nBytesRead != -1) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
                System.out.print(".");
                if (count++ > 64) break;
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    public static void testPlay(String filename) {
        try {
            File file = new File(filename);
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din = null;

            AudioFormat baseFormat = in.getFormat();
// getOutputFormat
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
// eof
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            // Play now.
            rawplay(decodedFormat, din);
            in.close();
        } catch (Exception e) {
            //Handle exception.
            System.out.println("Audio Exception:"+e);
            System.out.println("Add MP3 libraries: jl1.0.1.jar, mp3spi1.9.5.jar, tritonus_share.jar");
            System.out.println("They will be used automatically by JSDK13services.getProvidrs()");
        }
    }
}