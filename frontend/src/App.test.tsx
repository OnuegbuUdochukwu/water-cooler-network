import { render } from "@testing-library/react";
import App from "./App";

jest.mock("axios", () => ({
  __esModule: true,
  default: {
    get: jest.fn().mockResolvedValue({ data: null }),
    post: jest.fn().mockResolvedValue({ data: { token: "", user: null } }),
  },
}));

test("renders application without crashing", () => {
  render(<App />);
});


