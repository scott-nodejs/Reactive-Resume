/// <reference types='vitest' />

import { lingui } from "@lingui/vite-plugin";
import { nxViteTsPaths } from "@nx/vite/plugins/nx-tsconfig-paths.plugin";
import react from "@vitejs/plugin-react";
import { defineConfig, searchForWorkspaceRoot } from "vite";

export default defineConfig({
  cacheDir: "../../node_modules/.vite/client",

  build: {
    sourcemap: true,
    emptyOutDir: true,
  },

  define: {
    appVersion: JSON.stringify(process.env.npm_package_version),
  },

  server: {
    host: true,
    port: 5173,
    fs: { allow: [searchForWorkspaceRoot(process.cwd())] },
  },

  optimizeDeps: {
    esbuildOptions: {
      loader: {
        ".po": "text",
      },
    },
  },

  plugins: [
    react({
      babel: {
        plugins: ["macros"],
      },
    }),
    lingui(),
    nxViteTsPaths(),
    /**
     * 在仅启动 client 的开发环境下，转换认证提供者响应格式。
     * Java 后端返回对象格式，前端期望数组格式。
     */
    {
      name: "mock-auth-apis",
      configureServer(server) {
        // Mock auth providers endpoint
        server.middlewares.use("/api/auth/providers", (req, res, next) => {
          if (req.method === "GET") {
            // 模拟 Java 后端返回的格式，转换为前端期望的数组格式
            const mockJavaResponse = {
              code: 200,
              message: "操作成功",
              data: {
                github: true,
                google: true,
                openid: false,
                openidName: null
              },
              timestamp: Date.now()
            };
            
            const providers = [];
            if (mockJavaResponse.data.github) providers.push("github");
            if (mockJavaResponse.data.google) providers.push("google");
            if (mockJavaResponse.data.openid) providers.push("openid");
            providers.unshift("email");
            
            res.setHeader("Content-Type", "application/json");
            res.end(JSON.stringify(providers));
            return;
          }
          next();
        });

      },
    },
  ],

  test: {
    globals: true,
    environment: "jsdom",
    include: ["src/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"],
  },

  resolve: {
    alias: {
      "@/client/": `${searchForWorkspaceRoot(process.cwd())}/apps/client/src/`,
    },
  },
});
