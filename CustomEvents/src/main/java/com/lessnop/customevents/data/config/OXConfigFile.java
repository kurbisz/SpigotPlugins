package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.ox.OXManager;
import com.lessnop.customevents.ox.Question;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class OXConfigFile extends SimpleYamlFile {

	public OXConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		OXManager oxManager = CustomEvents.getInstance().getOxManager();
		BarColor barColor = BarColor.BLUE;
		try {
			barColor = BarColor.valueOf(config.getString("bossBarColor"));
		} catch (IllegalArgumentException | NullPointerException e) {}
		oxManager.setBarColor(barColor);
		BarStyle barStyle = BarStyle.SOLID;
		try {
			barStyle = BarStyle.valueOf(config.getString("bossBarStyle"));
		} catch (IllegalArgumentException | NullPointerException e) {}
		oxManager.setBarStyle(barStyle);

		int questionTime = config.getInt("questionTime");
		oxManager.setQuestionTime(questionTime);

		List<Question> questions = new ArrayList<>();
		ConfigurationSection questionsSection = config.getConfigurationSection("questions");
		for (String key : questionsSection.getKeys(false)) {
			ConfigurationSection singleQuestionSection = questionsSection.getConfigurationSection(key);
			questions.add(getQuestion(singleQuestionSection));
		}
		oxManager.setQuestions(questions);

		String yesRegionName = config.getString("correctRegion");
		oxManager.setYesRegionName(yesRegionName);
		String noRegionName = config.getString("wrongRegion");
		oxManager.setNoRegionName(noRegionName);

		List<List<String>> rewardsCommands = new ArrayList<>();
		ConfigurationSection rewardsSection = config.getConfigurationSection("rewards");
		for (String key : rewardsSection.getKeys(false)) {
			List<String> commands = rewardsSection.getStringList(key);
			rewardsCommands.add(commands);
		}
		oxManager.setRewardsCommands(rewardsCommands);

	}

	private Question getQuestion(ConfigurationSection section) {
		String question = StringUtils.replaceColors(section.getString("question"));
		boolean answer = section.getBoolean("answer");
		return new Question(question, answer);
	}


}
