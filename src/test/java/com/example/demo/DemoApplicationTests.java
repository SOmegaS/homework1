package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	HashMap createAccount() throws Exception {
		String json =
				mockMvc.perform(post("/createAccount?balance=1000&password=password")
								.with(user("admin").password("password").roles("ADMIN"))
						)
						.andExpect(status().is2xxSuccessful())
						.andReturn().getResponse().getContentAsString();
		return new ObjectMapper().readValue(json, HashMap.class);
	}

	@Test
	void testCreateAccountByAdmin() throws Exception {
		mockMvc.perform(post("/createAccount?balance=2000&password=password")
						.with(user("admin").password("password").roles("ADMIN"))
				)
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testCreateAccountByUser() throws Exception {
		HashMap account = createAccount();

		mockMvc.perform(post("/createAccount?balance=2000&password=password")
						.with(user(account.get("id").toString()).password("password"))
				)
				.andExpect(status().is4xxClientError());
	}

	@Test
	void testGetAccountByUser() throws Exception {
		HashMap account = createAccount();

		mockMvc.perform(get("/getAccount?id=" + account.get("id").toString())
						.with(user(account.get("id").toString()).password("password"))
				)
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testGetAccountByWrongUser() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		mockMvc.perform(get("/getAccount?id=" + account1.get("id").toString())
						.with(user(account2.get("id").toString()).password("password"))
				)
				.andExpect(status().is4xxClientError());
	}

	@Test
	void testGetAccountByAdmin() throws Exception {
		HashMap account = createAccount();

		mockMvc.perform(get("/getAccount?id=" + account.get("id").toString())
						.with(user("admin").password("password").roles("ADMIN"))
				)
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testCreateTransactionByUser() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		mockMvc.perform(post(
				"/createTransaction?sender="
						+ account1.get("id").toString()
						+ "&receiver="
						+ account2.get("id").toString()
						+ "&money="
						+ "123"
				)
				.with(user(account1.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testCreateTransactionByWrongUser() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		mockMvc.perform(post(
						"/createTransaction?sender="
								+ account1.get("id").toString()
								+ "&receiver="
								+ account2.get("id").toString()
								+ "&money="
								+ "123"
				)
						.with(user(account2.get("id").toString()).password("password")))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void testCreateTransactionByAdmin() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		mockMvc.perform(post(
						"/createTransaction?sender="
								+ account1.get("id").toString()
								+ "&receiver="
								+ account2.get("id").toString()
								+ "&money="
								+ "123"
				)
						.with(user("admin").password("password").roles("ADMIN")))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void testCancelTransactionBySender() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		String json = mockMvc.perform(post(
						"/createTransaction?sender="
								+ account1.get("id").toString()
								+ "&receiver="
								+ account2.get("id").toString()
								+ "&money="
								+ "123"
				)
						.with(user(account1.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		HashMap resp = new ObjectMapper().readValue(json, HashMap.class);

		mockMvc.perform(post("/cancelTransaction?id=" + resp.get("id").toString())
				.with(user(account1.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testCancelTransactionByReceiver() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		String json = mockMvc.perform(post(
						"/createTransaction?sender="
								+ account1.get("id").toString()
								+ "&receiver="
								+ account2.get("id").toString()
								+ "&money="
								+ "123"
				)
						.with(user(account1.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		HashMap resp = new ObjectMapper().readValue(json, HashMap.class);

		mockMvc.perform(post("/cancelTransaction?id=" + resp.get("id").toString())
						.with(user(account2.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testCancelTransactionByAdmin() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();

		String json = mockMvc.perform(post(
						"/createTransaction?sender="
								+ account1.get("id").toString()
								+ "&receiver="
								+ account2.get("id").toString()
								+ "&money="
								+ "123"
				)
						.with(user(account1.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		HashMap resp = new ObjectMapper().readValue(json, HashMap.class);

		mockMvc.perform(post("/cancelTransaction?id=" + resp.get("id").toString())
						.with(user("admin").password("password")))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void testCancelTransactionByWrongUser() throws Exception {
		HashMap account1 = createAccount();
		HashMap account2 = createAccount();
		HashMap account3 = createAccount();

		String json = mockMvc.perform(post(
						"/createTransaction?sender="
								+ account1.get("id").toString()
								+ "&receiver="
								+ account2.get("id").toString()
								+ "&money="
								+ "123"
				)
						.with(user(account1.get("id").toString()).password("password")))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		HashMap resp = new ObjectMapper().readValue(json, HashMap.class);

		mockMvc.perform(post("/cancelTransaction?id=" + resp.get("id").toString())
						.with(user(account3.get("id").toString()).password("password")))
				.andExpect(status().is4xxClientError());
	}
}
