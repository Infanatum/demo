package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataController.class)
public class XGamesDemoApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testSetData() throws Exception {
		mockMvc.perform(post("/api/set/user1/id1")
						.contentType(MediaType.TEXT_PLAIN)
						.content("test data"))
				.andExpect(status().isOk())
				.andExpect(content().string("Data stored successfully"));
	}

	@Test
	public void testGetDataFound() throws Exception {
		mockMvc.perform(post("/api/set/user1/id1")
				.contentType(MediaType.TEXT_PLAIN)
				.content("test data"));

		mockMvc.perform(get("/api/get/user1/id1"))
				.andExpect(status().isOk())
				.andExpect(content().string("test data"));
	}

	@Test
	public void testGetDataNotFound() throws Exception {
		mockMvc.perform(get("/api/get/user1/id2"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Data not found"));
	}
}
