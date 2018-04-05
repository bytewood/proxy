package bytewood.proxy;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@ShellComponent("Get Url Throu Proxy")
public class GetUrlThruProxyCommand {

    @ShellMethod("Return the body of a url through a proxy")
    public String geturl(final String url,
                         @ShellOption(value = {"-h", "--proxy-host"}, defaultValue = "") final String proxyHost,
                         @ShellOption(value = {"-p", "--proxy-port"}, defaultValue = "0") final int proxyPort,
                         @ShellOption(value = {"-u", "--proxy-user"}, defaultValue = "") final String proxyUser,
                         @ShellOption(value = {"-x", "--proxy-pass"}, defaultValue = "") final String proxyPass
    ) throws Exception {
        if (!StringUtils.isEmpty(proxyUser)) {
            authenticate(proxyUser, proxyPass);
        }


        final URL parseUrl = URI.create(url).toURL();
        final URLConnection conn;
        if (!StringUtils.isEmpty(proxyHost)) {
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            conn = parseUrl.openConnection(proxy);
        } else {
            conn = parseUrl.openConnection();
        }
        return StreamUtils.copyToString(conn.getInputStream(), StandardCharsets.UTF_8);
    }

    private void authenticate(final String username, final String password) {
        Authenticator authenticator = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication(username, password.toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);
    }

}
