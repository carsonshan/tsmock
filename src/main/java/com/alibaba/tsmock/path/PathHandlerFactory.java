package com.alibaba.tsmock.path;

import static io.undertow.servlet.Servlets.servlet;

import java.io.File;

import javax.servlet.ServletException;

import com.alibaba.tsmock.core.http.HttpMockServerHelper;
import com.alibaba.tsmock.core.http.HttpMockRequestHandler;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

public final class PathHandlerFactory {

	private PathHandlerFactory() {
	}

	public static PathHandler create() {
		PathHandler pathHandler = null;
		try {
			final DeploymentInfo deploymentInfo = Servlets.deployment()
					.setClassLoader(HttpMockServerHelper.class.getClassLoader()).setContextPath("/ui")
					.setDeploymentName("tsmock.war").setResourceManager(new FileResourceManager(new File("ui"), 1024))
					.addServlets(servlet("org.glassfish.jersey.servlet.ServletContainer",
							org.glassfish.jersey.servlet.ServletContainer.class)
									.addInitParam("jersey.config.core.provider.packages",
											"com.alibaba.tsmock.api.rest")
									.addMapping("/ws/*"));

			final DeploymentManager manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
			manager.deploy();

			final HttpHandler uiHandler = manager.start();
			pathHandler = Handlers.path().addPrefixPath("/ui", uiHandler).addPrefixPath("/",
					new HttpMockRequestHandler());
		} catch (final ServletException se) {

		}
		return pathHandler;

	}

}