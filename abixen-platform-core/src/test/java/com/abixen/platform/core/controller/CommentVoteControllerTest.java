/**
 * Copyright (c) 2010-present Abixen Systems. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.abixen.platform.core.controller;

import com.abixen.platform.core.configuration.PlatformConfiguration;
import com.abixen.platform.core.dto.FormErrorDto;
import com.abixen.platform.core.form.CommentVoteForm;
import com.abixen.platform.core.model.enumtype.CommentVoteType;
import com.abixen.platform.core.model.impl.Comment;
import com.abixen.platform.core.service.CommentVoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PlatformConfiguration.class)
public class CommentVoteControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Mock
    private CommentVoteService commentVoteService;

    @InjectMocks
    private CommentVoteController controller;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    public void testCreateCommentVote() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Comment savedComment = new Comment();
        savedComment.setId(10L);
        savedComment.setMessage("Test Comment Parent");
        CommentVoteForm savedCommentVoteForm = new CommentVoteForm();
        savedCommentVoteForm.setComment(savedComment);
        savedCommentVoteForm.setCommentVoteType(CommentVoteType.POSITIVE);
        savedCommentVoteForm.setId(10L);

        Comment inputComment = new Comment();
        inputComment.setMessage("Test Comment Parent");
        CommentVoteForm inputCommentVoteForm = new CommentVoteForm();
        inputCommentVoteForm.setComment(inputComment);
        inputCommentVoteForm.setCommentVoteType(CommentVoteType.POSITIVE);

        when(commentVoteService.saveCommentVote(any(CommentVoteForm.class))).thenReturn(savedCommentVoteForm);

        MvcResult commentsResponse = this.mockMvc.perform(post("/api/comment-votes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputCommentVoteForm))
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();
        JSONObject jsonObject = new JSONObject(commentsResponse.getResponse().getContentAsString());

        CommentVoteForm resForm = mapper.readValue(jsonObject.get("form").toString(), CommentVoteForm.class);
        List<FormErrorDto> validErrors = mapper.readValue(jsonObject.get("formErrors").toString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, FormErrorDto.class));
        assertNotNull(resForm);
        assertTrue(validErrors.isEmpty());
        assertTrue(resForm.getId() == 10);
    }

    @Test
    public void testUpdateCommentVote() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Comment savedComment = new Comment();
        savedComment.setId(10L);
        savedComment.setMessage("Test Comment Parent");
        CommentVoteForm savedCommentVoteForm = new CommentVoteForm();
        savedCommentVoteForm.setComment(savedComment);
        savedCommentVoteForm.setCommentVoteType(CommentVoteType.POSITIVE);
        savedCommentVoteForm.setId(10L);
        Comment inputComment = new Comment();
        inputComment.setId(10L);
        inputComment.setMessage("Test Comment Parent");
        CommentVoteForm inputCommentVoteForm = new CommentVoteForm();
        inputCommentVoteForm.setId(10L);
        inputCommentVoteForm.setComment(inputComment);
        inputCommentVoteForm.setCommentVoteType(CommentVoteType.NEGATIVE);

        when(commentVoteService.updateCommentVote(any(CommentVoteForm.class))).thenReturn(savedCommentVoteForm);

        MvcResult commentsResponse = this.mockMvc.perform(put("/api/comment-votes/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputCommentVoteForm))
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();
        JSONObject jsonObject = new JSONObject(commentsResponse.getResponse().getContentAsString());

        CommentVoteForm resForm = mapper.readValue(jsonObject.get("form").toString(), CommentVoteForm.class);
        List<FormErrorDto> validErrors = mapper.readValue(jsonObject.get("formErrors").toString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, FormErrorDto.class));
        assertNotNull(resForm);
        assertTrue(validErrors.isEmpty());
        assertTrue(resForm.getCommentVoteType().equals(CommentVoteType.POSITIVE));
    }
}
