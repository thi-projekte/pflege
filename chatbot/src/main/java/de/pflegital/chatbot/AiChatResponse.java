package de.pflegital.chatbot;

public class AiChatResponse
{
	private String chatBotResponse;
	private FormData formData;
	private String memoryId;

	public String getChatBotResponse()
	{
		return chatBotResponse;
	}

	public void setChatBotResponse(String chatBotResponse)
	{
		this.chatBotResponse = chatBotResponse;
	}

	public FormData getFormData()
	{
		return formData;
	}

	public void setFormData(FormData formData)
	{
		this.formData = formData;
	}

	public String getMemoryId()
	{
		return memoryId;
	}

	public void setMemoryId(String memoryId)
	{
		this.memoryId = memoryId;
	}
}
