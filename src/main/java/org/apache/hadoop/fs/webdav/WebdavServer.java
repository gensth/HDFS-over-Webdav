/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.fs.webdav;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;
import java.io.FileInputStream;

public class WebdavServer {
	private static final Log LOG = LogFactory.getLog(WebdavServer.class);

    public static final String WEB_APP_CONTEXT = "webAppContext";
    private static final String DEFAULT_FS_NAME = "hdfs://127.0.0.1:8020";
    private static final String DEFAULT_LISTEN_ADDRESS = "0.0.0.0";
    private static final String DEFAULT_BIND_PORT = "19800";

    private Server webServer;

    public WebdavServer(String bindAddress, int port) throws Exception {
        LOG.info("Initializing webdav server");

        webServer = new Server();
        XmlConfiguration configuration = new XmlConfiguration(new FileInputStream("conf/jetty.xml"));
        configuration.configure(webServer);

        Connector connector=new SelectChannelConnector();
        connector.setPort(port);
        connector.setHost(bindAddress);
        webServer.setConnectors(new Connector[]{connector});
    }

    public void start() throws Exception {
        webServer.start();
    }

    public static void main(String[] args) throws Exception {
        String usage = "WebdavServer";
        String header = "Run a webdav interface to a hadoop filesystem.";
        Options options = new Options();
        options = buildGeneralOptions(options);
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(usage, header, options, "");
            return;
        }
        int port = Integer.parseInt(cmd.getOptionValue("port", DEFAULT_BIND_PORT));

        // we use this cheesy way of passing the configuration to the WebdavServlet because
        //  jetty-5 doesn't have a way to send it in the WebdavServlet constructor
        Configuration config = new Configuration();
        String fsDefaultName = cmd.getOptionValue("fs", DEFAULT_FS_NAME);
        if (fsDefaultName != null) {
            config.set("fs.default.name", fsDefaultName);
        }
        WebdavServlet.setConf(config);

        WebdavServer server = new WebdavServer(cmd.getOptionValue("l", DEFAULT_LISTEN_ADDRESS), port);
        LOG.info("Starting webdav server");
        server.start();
    }

	private static Options buildGeneralOptions(Options opts) {
		Option listenOpt = new Option("l", "listen", true, "address to listen to, default: " + DEFAULT_LISTEN_ADDRESS);
        listenOpt.setArgName("address");
		opts.addOption(listenOpt);

		Option portOpt = new Option("p", "port", true, "port to bind to, default: " + DEFAULT_BIND_PORT);
        portOpt.setArgName("port");
		opts.addOption(portOpt);

		Option fsOpt = new Option("n", "fs", true, "value for fs.default.name, default: " + DEFAULT_FS_NAME);
        fsOpt.setArgName("uri");
		opts.addOption(fsOpt);

		opts.addOption("h", "help", false, "print usage information");
        return opts;
	}
}
