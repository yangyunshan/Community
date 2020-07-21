package com.example.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根结点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加到后缀书
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败");
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode temp = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                temp.addSubNode(c, subNode);
            }

            temp = subNode;

            if (i == keyword.length() -1) {
                temp.setKeywordEnd(true);
            }

        }
    }

    /**
     * 过滤敏感词
     * @param text
     * @return
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1
        TrieNode temp = root;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuffer stringBuffer = new StringBuffer();

        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                if (temp == root) {
                    stringBuffer.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            temp = temp.getSubNode(c);
            if (temp == null) {
                stringBuffer.append(c);
                begin++;
                position++;
                temp = root;
            } else if (temp.isKeywordEnd) {
                stringBuffer.append(REPLACEMENT);
                position++;
                begin = position;
                temp = root;
            } else {
                position++;
            }

        }

        //将最后一批字符计入结果
        stringBuffer.append(begin);
        return stringBuffer.toString();
    }

    /**
     * 判断是否为特殊符号
     */
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode {
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点（key是下级字符，value是下级节点）
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public TrieNode getSubNode(Character key) {
            return subNode.get(key);
        }

        public void addSubNode(Character c, TrieNode value) {
            this.subNode.put(c, value);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }


    }
}
