package com.hqy.cloud.chatgpt.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnofficialApiChatChunk {
    private String conversationId;
    private String error;
    private Message message;

    @Data
    public static class Message {
        private String id;
        private Author author;
        private Double createTime;
        private Double updateTime;
        private Content content;
        private Boolean endTurn;
        private Integer weight;
        private Metadata metadata;
        private String recipient;
    }

    @Data
    public static class Content {
        private String contentType;
        private List<String> parts;
    }

    @Data
    public static class Metadata {
        private String messageType;
        private String modelSlug;
        private FinishDetails finishDetails;
    }

    @Data
    public static class FinishDetails {
        private String type;
        private String stop;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private String role;
        private String name;

        public static Author of(String role, String name) {
            return new Author(role, name);
        }

    }


}
