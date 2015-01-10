package denaro.nick.core.controller;

import denaro.nick.core.Focusable;

public interface ControllerListener extends Focusable
{
	public void actionPerformed(ControllerEvent event);
}
