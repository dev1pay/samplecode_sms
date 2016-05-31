package service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

@Path("/")
public class SmsCharging {
	
	@GET
	@Path("/SmsCharging")
	@Produces({MediaType.APPLICATION_JSON})
	public Response charging(
			@DefaultValue("0") @QueryParam("access_key") String accKey,
			@DefaultValue("0") @QueryParam("command") String command,
			@QueryParam("mo_message") String mo_message,
			@QueryParam("msisdn") String phone,
			@QueryParam("request_id") String request_id,
			@QueryParam("request_time") String request_time,
			@QueryParam("short_code") String short_code,
			@QueryParam("signature") String signature) {
		JSONObject json = new JSONObject();
		try {
			String secret = ""; // Secret Key do 1pay cung cap. Thay bang Secret Key cua ban.
			String signatureGen = generateSignature(accKey, command,
					mo_message, phone, request_id, request_time, short_code,
					secret);
			if (signature.equalsIgnoreCase(signatureGen)) {				
				json.put("status", 1);
				json.put("sms", "Send sms thanh cong");
			}else {
				json.put("status", 0);
				json.put("sms", "tin nhan sai cu phap");
			}
			json.put("type", "text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(200).entity(json.toString()).build();
	}

	public static String hmacDigest(String msg, String keyString, String algo) {
		String digest = "";
		try {
			if (keyString != null && keyString.length() > 0) {
				SecretKeySpec key = new SecretKeySpec(
						(keyString).getBytes("UTF-8"), algo);
				Mac mac = Mac.getInstance(algo);
				mac.init(key);
				byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
				StringBuffer hash = new StringBuffer();
				for (int i = 0; i < bytes.length; i++) {
					String hex = Integer.toHexString(0xFF & bytes[i]);
					if (hex.length() == 1) {
						hash.append('0');
					}
					hash.append(hex);
				}
				digest = hash.toString();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return digest;
	}

	public String generateSignature(String access_key, String command,
			String mo_message, String phone, String request_id,
			String request_time, String short_code, String secret) {
		String urlParameters = "";
		String signature = "";
		if (access_key != null && command != null && mo_message != null
				&& phone != null && request_id != null && request_time != null
				&& short_code != null && secret != null) {
			urlParameters = "access_key=%access_key%&command=%command%&mo_message=%mo_message%"
					+ "&msisdn=%msisdn%&request_id=%request_id%&request_time=%request_time%&short_code=%short_code%";
			urlParameters = urlParameters.replaceFirst("%access_key%",
					access_key);
			urlParameters = urlParameters.replaceFirst("%command%", command);
			urlParameters = urlParameters.replaceFirst("%mo_message%",
					mo_message);
			urlParameters = urlParameters.replaceFirst("%msisdn%", phone);
			urlParameters = urlParameters.replaceFirst("%request_id%",
					request_id);
			urlParameters = urlParameters.replaceFirst("%request_time%",
					request_time);
			urlParameters = urlParameters.replaceFirst("%short_code%",
					short_code);

			signature = hmacDigest(urlParameters, secret, "HmacSHA256");
			System.out.println("Signature:" + signature);
		}
		return signature;
	}

	

}
