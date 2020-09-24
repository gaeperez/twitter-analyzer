package es.uvigo.ei.sing.twitteranalyzer.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@UtilityClass
public class Functions {

    public LocalDateTime convertToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public LocalDateTime convertToLocalDateTime(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime() : null;
    }

    public Date convertToDate(LocalDateTime date) {
        return date != null ? Date.from(date.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public long localDateTimeToUnix(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
    }

    public LocalDateTime getStartOfDay(LocalDateTime date) {
        return date.toLocalDate().atStartOfDay();
    }

    public LocalDateTime getEndOfDay(LocalDateTime date) {
        return date.toLocalDate().atTime(LocalTime.MAX);
    }

    public String makeSHA1Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = StringUtils.stripAccents(input).trim().toLowerCase().getBytes(StandardCharsets.UTF_8);
        md.update(buffer);
        byte[] digest = md.digest();

        StringBuilder hexStr = new StringBuilder();
        for (byte b : digest)
            hexStr.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));

        return hexStr.toString();
    }

    public void writeJson(Path path, Object object) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(object);

            Files.deleteIfExists(path);
            Files.write(path, Collections.singletonList(json), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T readJson(Path path, Class<T> toClass) {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(path.toAbsolutePath().toFile())) {
            // Convert JSON File to Java Object
            return gson.fromJson(reader, toClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String resolveUrl(String url) {
        String resolvedUrl;

        try {
            Connection.Response response = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .execute();
            Thread.sleep(500);

            if (url.contains("t.co/")) {
                // Get URL body
                String body = response.body();
                // Pattern to search
                String patternString = "URL=(.*)\">";
                Pattern pattern = Pattern.compile(patternString);

                Matcher matcher = pattern.matcher(body);
                matcher.find();
                resolvedUrl = matcher.group(1);
            } else
                resolvedUrl = response.url().toExternalForm();

            if (!resolvedUrl.equalsIgnoreCase(url))
                resolvedUrl = resolveUrl(resolvedUrl);
        } catch (Exception e) {
            log.error("Error resolving the url {}. See error: {}", url, e);
            resolvedUrl = url;
        }

        return resolvedUrl;
    }

    public String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public List<String> readStopWords() {
        List<String> toRet = new ArrayList<>();

        try (InputStream resource = Functions.class.getResourceAsStream("/stopwords.txt")) {
            toRet = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Cannot load stop words file... See error: {}", e.getLocalizedMessage());
        }

        return toRet;
    }

    public String htmlToText(String html) {
        return html != null ? Jsoup.parse(html).text() : null;
    }
}
