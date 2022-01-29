package ae.du.channel.payment.api.session;

import static com.cybersource.flex.android.CaptureContext.fromJwt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.cybersource.flex.android.CaptureContext;
import com.cybersource.flex.android.FlexException;
import com.cybersource.flex.android.FlexService;
import com.cybersource.flex.android.TransientToken;
import com.cybersource.flex.android.TransientTokenCreationCallback;
import com.google.gson.Gson;

@Component
public class SessionCallback implements Callback {

    @Autowired
    private Environment env;

    private static final Logger log = LoggerFactory.getLogger(SessionCallback.class);

    @Override
    public void success(final String response) {
        Objects.requireNonNull(response, "/session response cannot be null");

        final CaptureContext captureContext = fromJwt(response);
        final FlexService service = FlexService.getInstance();
        service.createTokenAsyncTask(captureContext, dummyCapturedCard(), new TransientTokenCreationCallback() {

            @Override
            public void onSuccess(final TransientToken response) {
                log.debug("/token response = {}", new Gson().toJson(response));
                // FE team will invoke the DXP GraphQL payment API here
            }

            @Override
            public void onFailure(final FlexException error) {
                log.error("/token failure", error);
            }
        });
    }

    @Override
    public void failure(final Exception ex) {

    }

    private Map<String, Object> dummyCapturedCard() {
        final Map<String, Object> card = new HashMap<>();
        card.put("paymentInformation.card.number", this.env.getProperty("paymentInformation.card.number"));
        card.put("paymentInformation.card.expirationMonth", this.env.getProperty("paymentInformation.card.expirationMonth"));
        card.put("paymentInformation.card.expirationYear", this.env.getProperty("paymentInformation.card.expirationYear"));
        card.put("paymentInformation.card.securityCode", this.env.getProperty("paymentInformation.card.securityCode"));
        card.put("paymentInformation.card.type", this.env.getProperty("paymentInformation.card.type"));

        return card;
    }
}

interface Callback {
    void success(final String captureContext);
    void failure(final Exception ex);
}
