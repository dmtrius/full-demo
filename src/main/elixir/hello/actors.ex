defmodule Hello.Actors do
  @moduledoc false
  
  use Application

  def start(_type, _args) do
    Hello.Actors.Supervisor.start_link()
  end
end