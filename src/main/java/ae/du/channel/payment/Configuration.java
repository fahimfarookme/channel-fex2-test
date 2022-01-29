package ae.du.channel.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class Configuration {

	private Merchant merchant;
	private Consts consts;

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Consts getConsts() {
		return consts;
	}

	public void setConsts(Consts consts) {
		this.consts = consts;
	}

	public static class Merchant {
		private String keyId;
		private String secretKey;
		private String id;
		private String url;
		private String requestTarget;
		private String authenticationType;
		private String requestHost;
		private String responseMessage;
		private String responseCode;
		private String requestType;
		private String runEnvironment;
		private String requestJsonPath;
		private String requestData;
		private Boolean useMetaKey;
		private String sessionUrl;
		private String algorithm;

		public String getKeyId() {
			return keyId;
		}

		public void setKeyId(String keyId) {
			this.keyId = keyId;
		}

		public String getSecretKey() {
			return secretKey;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getRequestTarget() {
			return requestTarget;
		}

		public void setRequestTarget(String requestTarget) {
			this.requestTarget = requestTarget;
		}

		public String getAuthenticationType() {
			return authenticationType;
		}

		public void setAuthenticationType(String authenticationType) {
			this.authenticationType = authenticationType;
		}

		public String getRequestHost() {
			return requestHost;
		}

		public void setRequestHost(String requestHost) {
			this.requestHost = requestHost;
		}

		public String getResponseMessage() {
			return responseMessage;
		}

		public void setResponseMessage(String responseMessage) {
			this.responseMessage = responseMessage;
		}

		public String getResponseCode() {
			return responseCode;
		}

		public void setResponseCode(String responseCode) {
			this.responseCode = responseCode;
		}

		public String getRequestType() {
			return requestType;
		}

		public void setRequestType(String requestType) {
			this.requestType = requestType;
		}

		public String getRunEnvironment() {
			return runEnvironment;
		}

		public void setRunEnvironment(String runEnvironment) {
			this.runEnvironment = runEnvironment;
		}

		public String getRequestJsonPath() {
			return requestJsonPath;
		}

		public void setRequestJsonPath(String requestJsonPath) {
			this.requestJsonPath = requestJsonPath;
		}

		public String getRequestData() {
			return requestData;
		}

		public void setRequestData(String requestData) {
			this.requestData = requestData;
		}

		public Boolean getUseMetaKey() {
			return useMetaKey;
		}

		public void setUseMetaKey(Boolean useMetaKey) {
			this.useMetaKey = useMetaKey;
		}

		public String getSessionUrl() {
			return sessionUrl;
		}

		public void setSessionUrl(String sessionUrl) {
			this.sessionUrl = sessionUrl;
		}

		public String getAlgorithm() {
			return algorithm;
		}

		public void setAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}
	}

	public static class Consts {
		private String get;
		private String post;
		private String space;
		private String http;
		private String jwt;
		private String vcMerchantId;
		private String date;
		private String host;
		private String digest;
		private String accept;
		private String signature;
		private String postHeader;
		private String getHeader;

		public String getGet() {
			return get;
		}

		public void setGet(String get) {
			this.get = get;
		}

		public String getPost() {
			return post;
		}

		public void setPost(String post) {
			this.post = post;
		}

		public String getSpace() {
			return space;
		}

		public void setSpace(String space) {
			this.space = space;
		}

		public String getHttp() {
			return http;
		}

		public void setHttp(String http) {
			this.http = http;
		}

		public String getJwt() {
			return jwt;
		}

		public void setJwt(String jwt) {
			this.jwt = jwt;
		}

		public String getVcMerchantId() {
			return vcMerchantId;
		}

		public void setVcMerchantId(String vcMerchantId) {
			this.vcMerchantId = vcMerchantId;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getDigest() {
			return digest;
		}

		public void setDigest(String digest) {
			this.digest = digest;
		}

		public String getAccept() {
			return accept;
		}

		public void setAccept(String accept) {
			this.accept = accept;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public String getPostHeader() {
			return postHeader;
		}

		public void setPostHeader(String postHeader) {
			this.postHeader = postHeader;
		}

		public String getGetHeader() {
			return getHeader;
		}

		public void setGetHeader(String getHeader) {
			this.getHeader = getHeader;
		}
	}

}
