package com.jaoafa.Newsjao;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONArray;
import org.json.JSONObject;

import am.ik.voicetext4j.EmotionalSpeaker;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.audio.AudioPlayer;

public class NewsSpeak {
	public static IMessage newsmessage = null;
	public static void speakNews(IDiscordClient client, IChannel channel, IVoiceChannel voice, boolean morning){
		if(voice == null) return;
		voice.join();

		AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(voice.getGuild());
		audioP.clear();

		if(Newsjao.newsapikey == null){
			return;
		}

		JSONObject json = getHttpsJson("https://newsapi.org/v2/top-headlines?country=jp&apiKey=" + Newsjao.newsapikey, null);
		if(!json.getString("status").equalsIgnoreCase("ok")){
			AudioInputStream stream = EmotionalSpeaker.HARUKA.ready().happy().getResponse("ニュースを取得できませんでした。").audioInputStream();
			audioP.queue(stream);
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("HH時mm分");

		List<String> List = new LinkedList<>();

		if(morning){
			List.add("おはようございます。");
		}
		List.add(format.format(new Date()) + "のヘッドラインニュースをお知らせします。");

		JSONArray jsonArr = json.getJSONArray("articles");
		List<String> NewsTitle = new LinkedList<>();
		Map<String, String> EmbedTextList = new LinkedHashMap<>();
		jsonArr.forEach(o -> {
			if (!(o instanceof JSONObject)){
				return;
			}
			if(NewsTitle.size() >= 5){
				return;
			}
			JSONObject one = (JSONObject) o;
			if(one.isNull("title") || one.isNull("content")){
				return;
			}
			String title = one.getString("title");
			String content = one.getString("content");
			content = content.substring(0, content.lastIndexOf("。") + 1); // 最後の。までを取得

			int i = NewsTitle.size() + 1;

			List.add(i + "番目のニュースです。");
			List.add("「" + title + "」");
			content = formatSpeakContent(content);
			List.add(content);
			NewsTitle.add(title);

			EmbedTextList.put(title, content);
		});

		for(String msg : List){
			try {
				if(msg.contains("。")){
					for(String one : msg.split("。")){
						AudioInputStream stream = EmotionalSpeaker.HIKARI.ready().speed(90).happy().getResponse(one + "。").audioInputStream();
						audioP.queue(stream);

						if(new File("mute0.5.mp3").exists()){
							audioP.queue(new File("mute0.5.mp3"));
						}
					}
				}else{
					AudioInputStream stream = EmotionalSpeaker.HIKARI.ready().speed(90).happy().getResponse(msg).audioInputStream();
					audioP.queue(stream);

					if(new File("mute0.5.mp3").exists()){
						audioP.queue(new File("mute0.5.mp3"));
					}
				}


				if(new File("mute1.mp3").exists()){
					audioP.queue(new File("mute1.mp3"));
				}
			}catch(IllegalArgumentException | IOException | UnsupportedAudioFileException e){
				e.printStackTrace();
			}
		}

		EmbedBuilder embed = new EmbedBuilder();
		embed.withTitle("jaotan - News");
		embed.withAuthorIcon(client.getApplicationIconURL());
		embed.withAuthorName("jaotan");
		embed.withAuthorUrl("https://github.com/book000/Newsjao");
		embed.appendDescription("Generalボイスチャンネルでニュースの放送を開始しました。");

		for (Map.Entry<String, String> one : EmbedTextList.entrySet()) {
			embed.appendField(one.getKey(), one.getValue(), false);
		}

		embed.withColor(Color.GREEN);

		if(newsmessage != null && !newsmessage.isDeleted()){
			newsmessage.delete();
		}

		newsmessage = channel.sendMessage(embed.build());
	}

	static String formatSpeakContent(String text){
		Pattern pattern = Pattern.compile("【[^【】]*?】");

        String after = null;
        while (true) {
            after = pattern.matcher(text).replaceAll("");
            if (text.equals(after)) {
                break;
            }
            text = after;
        }


        return text;
	}

	protected static JSONObject getHttpsJson(String address, Map<String, String> headers){
		StringBuilder builder = new StringBuilder();
		try{
			URL url = new URL(address);

			HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();
			connect.setRequestMethod("GET");
			if(headers != null){
				for(Map.Entry<String, String> header : headers.entrySet()) {
					connect.setRequestProperty(header.getKey(), header.getValue());
				}
			}

			connect.connect();

			if(connect.getResponseCode() != HttpURLConnection.HTTP_OK){
				InputStream in = connect.getErrorStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				in.close();
				connect.disconnect();

				System.out.println("ConnectWARN: " + connect.getResponseMessage());
				return null;
			}

			InputStream in = connect.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			in.close();
			connect.disconnect();
			JSONObject json = new JSONObject(builder.toString());
			return json;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
